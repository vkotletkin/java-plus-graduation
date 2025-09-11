package ru.practicum.application.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.repository.LocationRepository;
import ru.practicum.application.event.service.AdminEventService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.ewm.stats.proto.InteractionsCountRequestProto;
import ru.practicum.ewm.stats.proto.RecommendedEventProto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.request.event.UpdateEventAdminRequest;
import ru.practicum.stats.client.AnalyzerGrpcClient;
import ru.practicum.stats.client.CategoryFeignClient;
import ru.practicum.stats.client.RequestFeignClient;
import ru.practicum.stats.client.UserFeignClient;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.exception.NotFoundException.notFoundException;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final UserFeignClient userFeignClient;
    private final CategoryFeignClient categoryFeignClient;
    private final RequestFeignClient requestFeignClient;
    private final AnalyzerGrpcClient analyzerGrpcClient;

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) throws ValidationException {

        List<EventFullDto> eventDtos;
        List<EventState> eventStateList;

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Время начала поиска позже времени конца поиска");
        }

        if ((states == null) || (states.isEmpty())) {
            eventStateList = Arrays.stream(EventState.values()).toList();
        } else {
            eventStateList = states.stream().map(EventState::valueOf).toList();
        }

        if (users == null && categories == null) {

            Map<Long, Event> allEventsWithDates = new ArrayList<>(eventRepository.findAll(PageRequest.of(from / size, size)).getContent())
                    .stream().collect(Collectors.toMap(Event::getId, e -> e));

            List<EventRequestDto> requestsByEventIds = requestFeignClient.findByEventIds(allEventsWithDates.values().stream()
                    .mapToLong(Event::getId).boxed().toList());

            List<Long> usersIds = allEventsWithDates.values().stream().map(Event::getInitiator).toList();

            Set<Long> categoriesIds = allEventsWithDates.values()
                    .stream().map(Event::getCategory).collect(Collectors.toSet());

            Map<Long, UserDto> usersByRequests = userFeignClient.getUsersList(usersIds, 0, Math.max(allEventsWithDates.size(), 1))
                    .stream()
                    .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

            Map<Long, CategoryDto> categoriesByRequests = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                    .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

            eventDtos = allEventsWithDates.values().stream()
                    .map(e -> EventMapper.mapEventToFullDto(e,
                            requestsByEventIds.stream()
                                    .filter(r -> r.getId().equals(e.getId()))
                                    .count(),
                            categoriesByRequests.get(e.getCategory()),
                            usersByRequests.get(e.getInitiator())))
                    .toList();

        } else {

            Map<Long, Event> allEventsWithDates = eventRepository.findAllEventsWithDates(users,
                            eventStateList, categories, rangeStart, rangeEnd,
                            PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")))
                    .stream().collect(Collectors.toMap(Event::getId, e -> e));

            List<EventRequestDto> requestsByEventIds = requestFeignClient.findByEventIds(allEventsWithDates.values().stream()
                    .mapToLong(Event::getId).boxed().collect(Collectors.toList()));

            List<Long> usersIds = allEventsWithDates.values().stream().map(Event::getInitiator).toList();
            Set<Long> categoriesIds = allEventsWithDates.values()
                    .stream().map(Event::getCategory).collect(Collectors.toSet());

            Map<Long, UserDto> usersByRequests = userFeignClient.getUsersList(usersIds, 0, Math.max(allEventsWithDates.size(), 1))
                    .stream()
                    .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

            Map<Long, CategoryDto> categoriesByRequests = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                    .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

            eventDtos = allEventsWithDates.values().stream()
                    .map(e -> EventMapper.mapEventToFullDto(e,
                            requestsByEventIds.stream()
                                    .filter(r -> r.getEvent().equals(e.getId()))
                                    .count(),
                            categoriesByRequests.get(e.getCategory()),
                            usersByRequests.get(e.getInitiator())))
                    .toList();
        }

        if (!eventDtos.isEmpty()) {

            ArrayList<Long> longs = eventDtos.stream()
                    .map(EventFullDto::getId).collect(Collectors.toCollection(ArrayList::new));
            List<EventRequestDto> requests = requestFeignClient.getByEventAndStatus(longs, "CONFIRMED");
            Map<Long, Double> eventRating = analyzerGrpcClient.getInteractionsCount(getInteractionsRequest(longs)).stream()
                    .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

            return eventDtos.stream()
                    .peek(dto -> dto.setConfirmedRequests(
                            requests.stream()
                                    .filter((request -> request.getEvent().equals(dto.getId())))
                                    .count()
                    ))
                    .peek(dto -> eventRating.getOrDefault(dto.getId(), 0.0))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateRequest) throws ConflictException, ValidationException, NotFoundException, WrongDataException {

        Event event = eventRepository.findById(eventId).orElseThrow(notFoundException("Событие не существует. Идентификатор события: {0}", eventId));

        if (LocalDateTime.now().isAfter(event.getEventDate().minus(2, ChronoUnit.HOURS))) {
            throw new ConflictException("До начала события меньше часа, изменение события невозможно");
        }

        if (!event.getState().equals(EventState.PENDING)) {
            throw new ConflictException("Событие не в состоянии \"Ожидание публикации\", изменение события невозможно");
        }

        updateEventWithAdminRequest(event, updateRequest);
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Событие уже завершилось");
        }

        saveLocation(event);
        event = eventRepository.save(event);
        EventFullDto eventFullDto = getEventFullDto(event);

        return getViewsCounter(eventFullDto);
    }

    private EventFullDto getEventFullDto(Event event) throws NotFoundException {
        Long confirmed = requestFeignClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        return EventMapper.mapEventToFullDto(event, confirmed, categoryFeignClient.getCategoryById(event.getCategory()),
                userFeignClient.getById(event.getInitiator()));
    }

    private void updateEventWithAdminRequest(Event event, UpdateEventAdminRequest updateRequest) throws NotFoundException, WrongDataException {

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            if (!categoryFeignClient.existById(updateRequest.getCategory())) {
                throw new NotFoundException("Категория не найдена. Идентификатор категории: {0}", updateRequest.getCategory());
            }
            event.setCategory(updateRequest.getCategory());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(updateRequest.getEventDate(), DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN)));
        }

        if (updateRequest.getLocation() != null) {
            event.setLocation(EventMapper.mapDtoToLocation(updateRequest.getLocation()));
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new WrongDataException("Неверное состояние события, не удалось обновить");
            }
        }
        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }
    }

    void saveLocation(Event event) {
        event.setLocation(locationRepository.save(event.getLocation()));
    }

    private EventFullDto getViewsCounter(EventFullDto eventFullDto) {

        List<RecommendedEventProto> protos = analyzerGrpcClient.getInteractionsCount(
                InteractionsCountRequestProto.newBuilder().addAllEventId((List.of(eventFullDto.getId()))).build());

        Double rating = protos.isEmpty() ? 0.0 : protos.getFirst().getScore();
        eventFullDto.setRating(rating);
        return eventFullDto;
    }

    private InteractionsCountRequestProto getInteractionsRequest(List<Long> eventId) {
        return InteractionsCountRequestProto.newBuilder().addAllEventId(eventId).build();
    }
}
