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
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                        Integer from, Integer size) throws ValidationException {

        validateTimeRange(rangeStart, rangeEnd);
        List<EventState> eventStateList = parseEventStates(states);

        Map<Long, Event> events = fetchEvents(users, eventStateList, categories, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<EventFullDto> eventDtos = convertEventsToDtos(events);
        return enrichEventsWithAdditionalData(eventDtos);
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

    private void validateTimeRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) throws ValidationException {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Время начала поиска позже времени конца поиска");
        }
    }

    private List<EventState> parseEventStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return Arrays.stream(EventState.values()).toList();
        }
        return states.stream().map(EventState::valueOf).toList();
    }

    private Map<Long, Event> fetchEvents(List<Long> users, List<EventState> eventStates,
                                         List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Integer from, Integer size) {

        if (users == null && categories == null) {
            return eventRepository.findAll(PageRequest.of(from / size, size))
                    .getContent()
                    .stream()
                    .collect(Collectors.toMap(Event::getId, e -> e));
        }

        return eventRepository.findAllEventsWithDates(users, eventStates, categories,
                        rangeStart, rangeEnd,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "e.eventDate")))
                .stream()
                .collect(Collectors.toMap(Event::getId, e -> e));
    }

    private List<EventFullDto> convertEventsToDtos(Map<Long, Event> events) {

        List<Long> eventIds = extractEventIds(events);
        List<Long> userIds = extractUserIds(events);
        Set<Long> categoryIds = extractCategoryIds(events);

        List<EventRequestDto> requests = requestFeignClient.findByEventIds(eventIds);
        Map<Long, UserDto> users = fetchUsers(userIds, events.size());
        Map<Long, CategoryDto> categories = fetchCategories(categoryIds);

        return events.values().stream()
                .map(event -> mapEventToFullDto(event, requests, users, categories))
                .toList();
    }

    private List<Long> extractEventIds(Map<Long, Event> events) {
        return events.values().stream()
                .map(Event::getId)
                .toList();
    }

    private List<Long> extractUserIds(Map<Long, Event> events) {
        return events.values().stream()
                .map(Event::getInitiator)
                .toList();
    }

    private Set<Long> extractCategoryIds(Map<Long, Event> events) {
        return events.values().stream()
                .map(Event::getCategory)
                .collect(Collectors.toSet());
    }

    private Map<Long, UserDto> fetchUsers(List<Long> userIds, int eventsSize) {
        return userFeignClient.getUsersList(userIds, 0, Math.max(eventsSize, 1))
                .stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
    }

    private Map<Long, CategoryDto> fetchCategories(Set<Long> categoryIds) {
        return categoryFeignClient.getCategoriesByIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));
    }

    private EventFullDto mapEventToFullDto(Event event, List<EventRequestDto> requests,
                                           Map<Long, UserDto> users, Map<Long, CategoryDto> categories) {

        long confirmedRequestsCount = countConfirmedRequestsForEvent(requests, event.getId());
        CategoryDto categoryDto = categories.get(event.getCategory());
        UserDto userDto = users.get(event.getInitiator());

        return EventMapper.mapEventToFullDto(event, confirmedRequestsCount, categoryDto, userDto);
    }

    private long countConfirmedRequestsForEvent(List<EventRequestDto> requests, Long eventId) {
        return requests.stream()
                .filter(request -> request.getEvent().equals(eventId))
                .count();
    }

    private List<EventFullDto> enrichEventsWithAdditionalData(List<EventFullDto> eventDtos) {
        List<Long> eventIds = eventDtos.stream()
                .map(EventFullDto::getId)
                .toList();

        List<EventRequestDto> confirmedRequests = requestFeignClient.getByEventAndStatus(eventIds, "CONFIRMED");
        Map<Long, Double> eventRatings = fetchEventRatings(eventIds);

        return eventDtos.stream()
                .map(dto -> enrichSingleEvent(dto, confirmedRequests, eventRatings))
                .toList();
    }

    private Map<Long, Double> fetchEventRatings(List<Long> eventIds) {
        return analyzerGrpcClient.getInteractionsCount(getInteractionsRequest(eventIds))
                .stream()
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));
    }

    private EventFullDto enrichSingleEvent(EventFullDto dto, List<EventRequestDto> confirmedRequests,
                                           Map<Long, Double> eventRatings) {

        long confirmedCount = confirmedRequests.stream()
                .filter(request -> request.getEvent().equals(dto.getId()))
                .count();

        dto.setConfirmedRequests(confirmedCount);
        dto.setRating(eventRatings.getOrDefault(dto.getId(), 0.0));

        return dto;
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

    private void saveLocation(Event event) {
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
