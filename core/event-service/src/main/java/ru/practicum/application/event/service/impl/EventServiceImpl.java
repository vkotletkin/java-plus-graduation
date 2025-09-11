package ru.practicum.application.event.service.impl;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.service.EventService;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.stats.client.*;
import ru.practicum.util.JsonFormatPattern;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.exception.NotFoundException.notFoundException;


@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private static final String NOT_FOUND_EVENT_MESSAGE = "Событие с идентификатором: {0} - не найдено";
    private static final String PAGE_REQUEST_SORTING_PARAMETER = "e.eventDate";
    private static final String DATE_AND_TIME_VALIDATION_MESSAGE = "Дата и время начала поиска не должна быть позже даты и времени конца поиска";

    private final UserFeignClient userClient;
    private final CategoryFeignClient categoryClient;
    private final RequestFeignClient requestClient;
    private final CollectorGrpcClient collectorGrpcClient;
    private final AnalyzerGrpcClient analyzerGrpcClient;

    private final EventRepository eventRepository;

    @Override
    public EventFullDto getEventById(Long eventId, Long userId, String uri, String ip) throws NotFoundException {

        collectorGrpcClient.sendUserAction(
                createUserAction(eventId, userId, ActionTypeProto.ACTION_VIEW, Instant.now()));

        Event event = eventRepository.findById(eventId).orElseThrow(notFoundException(NOT_FOUND_EVENT_MESSAGE, eventId));

        if (!event.getState().equals(EventState.PUBLISHED) && !uri.toLowerCase().contains("admin")) {
            throw new NotFoundException("Такого события не существует");
        }

        long confirmed = requestClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));

        EventFullDto eventFullDto = EventMapper.mapEventToFullDto(
                event,
                confirmed,
                categoryClient.getCategoryById(event.getCategory()),
                userClient.getById(event.getInitiator())
        );

        List<RecommendedEventProto> proto = analyzerGrpcClient.getInteractionsCount(getInteractionsRequest(List.of(eventId)));

        Double rating = proto.isEmpty() ? 0.0 : proto.getFirst().getScore();
        eventFullDto.setRating(rating);

        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Integer from,
                                                 Integer size,
                                                 String uri,
                                                 String ip) throws ValidationException {

        boolean sortByDate = isSortByDate(sort);

        if (sortByDate) {
            return getEventsSortedByDate(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, from, size);
        } else {
            return getEventsSortedByViews(text, categories, paid, rangeStart, rangeEnd,
                    onlyAvailable, from, size);
        }
    }

    private boolean isSortByDate(String sort) {
        return "EVENT_DATE".equals(sort);
    }

    private List<EventShortDto> getEventsSortedByDate(String text,
                                                      List<Long> categories,
                                                      Boolean paid,
                                                      String rangeStart,
                                                      String rangeEnd,
                                                      Boolean onlyAvailable,
                                                      Integer from,
                                                      Integer size) throws ValidationException {

        List<Event> events;

        if (isDefaultDateRangeWithCategories(rangeStart, rangeEnd, categories)) {
            events = getEventsByCategoriesOnly(categories, from, size);
        } else {
            events = getEventsWithDateRange(text, rangeStart, rangeEnd, from, size);
        }

        return createShortEventDtos(events);
    }

    private boolean isDefaultDateRangeWithCategories(String rangeStart, String rangeEnd, List<Long> categories) {
        return rangeStart == null && rangeEnd == null && categories != null;
    }

    private List<Event> getEventsByCategoriesOnly(List<Long> categories, Integer from, Integer size) {
        return eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED,
                createPageRequest(from, size, Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER));
    }

    private List<Event> getEventsWithDateRange(String text,
                                               String rangeStart,
                                               String rangeEnd,
                                               Integer from,
                                               Integer size) throws ValidationException {

        LocalDateTime startDate = parseDateTime(rangeStart, LocalDateTime.now());
        String searchText = getSearchText(text);

        if (rangeEnd == null) {
            return getEventsByTextOnly(searchText, from, size);
        } else {
            LocalDateTime endDate = parseDateTime(rangeEnd, null);
            validateDateRange(startDate, endDate);
            return getEventsByTextAndDateRange(searchText, startDate, endDate, from, size);
        }
    }

    private List<EventShortDto> getEventsSortedByViews(String text,
                                                       List<Long> categories,
                                                       Boolean paid,
                                                       String rangeStart,
                                                       String rangeEnd,
                                                       Boolean onlyAvailable,
                                                       Integer from,
                                                       Integer size) throws ValidationException {

        LocalDateTime startDate = parseDateTime(rangeStart, LocalDateTime.now());
        LocalDateTime endDate = parseDateTime(rangeEnd, null);

        validateDateRangeIfProvided(rangeStart, rangeEnd, startDate, endDate);

        List<Event> events = eventRepository.findEventList(text, categories, paid,
                startDate, endDate, EventState.PUBLISHED);

        return createAndSortShortEventDtos(events, from, size);
    }

    private LocalDateTime parseDateTime(String dateTimeString, LocalDateTime defaultValue) {
        if (dateTimeString == null) {
            return defaultValue;
        }
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
    }

    private String getSearchText(String text) {
        return text == null ? "" : "%" + text.toLowerCase() + "%";
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) throws ValidationException {
        if (startDate.isAfter(endDate)) {
            throw new ValidationException(DATE_AND_TIME_VALIDATION_MESSAGE);
        }
    }

    private void validateDateRangeIfProvided(String rangeStart, String rangeEnd,
                                             LocalDateTime startDate, LocalDateTime endDate) throws ValidationException {
        if (rangeStart != null && rangeEnd != null && startDate.isAfter(endDate)) {
            throw new ValidationException(DATE_AND_TIME_VALIDATION_MESSAGE);
        }
    }

    private List<Event> getEventsByTextOnly(String searchText, Integer from, Integer size) {
        return eventRepository.findEventsByText(searchText, EventState.PUBLISHED,
                createPageRequest(from, size, Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER));
    }

    private List<Event> getEventsByTextAndDateRange(String searchText,
                                                    LocalDateTime startDate,
                                                    LocalDateTime endDate,
                                                    Integer from,
                                                    Integer size) {
        return eventRepository.findAllByTextAndDateRange(searchText, startDate, endDate, EventState.PUBLISHED,
                createPageRequest(from, size, Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER));
    }

    private PageRequest createPageRequest(Integer from, Integer size, Sort.Direction direction, String... properties) {
        return PageRequest.of(from / size, size, Sort.by(direction, properties));
    }

    private List<EventShortDto> createAndSortShortEventDtos(List<Event> events, Integer from, Integer size) {
        List<EventShortDto> shortEventDtos = createShortEventDtos(events);
        shortEventDtos.sort(Comparator.comparing(EventShortDto::getRating));
        return paginateList(shortEventDtos, from, size);
    }

    private <T> List<T> paginateList(List<T> list, Integer from, Integer size) {
        int toIndex = Math.min(from + size, list.size());
        return list.subList(from, toIndex);
    }

    @Override
    public List<EventFullDto> getRecommendations(Long userId) {

        List<Event> events = eventRepository.findAllById(
                analyzerGrpcClient.getRecommendationsForUser(
                        UserPredictionsRequestProto.newBuilder()
                                .setUserId(userId)
                                .setMaxResult(10)
                                .build()
                ).stream().map(RecommendedEventProto::getEventId).toList()
        );

        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();

        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());

        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return events.stream().map(e -> EventMapper.mapEventToFullDto(
                e,
                requestClient.countByEventAndStatuses(e.getId(), List.of("CONFIRMED")),
                categories.get(e.getCategory()),
                users.get(e.getInitiator())
        )).toList();
    }

    @Override
    public void likeEvent(Long eventId, Long userId) throws ValidationException {

        if (!requestClient.isUserTakePart(userId, eventId)) {
            throw new ValidationException("Пользователь " + userId + " не принимал участи в событии " + eventId);
        }

        collectorGrpcClient.sendUserAction(createUserAction(eventId, userId, ActionTypeProto.ACTION_LIKE, Instant.now()));
    }

    private UserActionProto createUserAction(Long eventId, Long userId, ActionTypeProto type, Instant timestamp) {
        return UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(type)
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(timestamp.getEpochSecond())
                        .setNanos(timestamp.getNano())
                        .build())
                .build();
    }

    private List<EventShortDto> createShortEventDtos(List<Event> events) {

        List<EventRequestDto> requests = requestClient.findByEventIds(new ArrayList<>(
                events.stream().map(Event::getId).toList()));

        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();
        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));
        InteractionsCountRequestProto proto = getInteractionsRequest(
                events.stream().map(Event::getId).toList());

        Map<Long, Double> eventRating = analyzerGrpcClient.getInteractionsCount(proto)

                .stream().collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));

        return events.stream()
                .map(e -> EventMapper.mapEventToShortDto(e, categories.get(e.getCategory()), users.get(e.getInitiator())))
                .peek(dto -> dto.setConfirmedRequests(
                        requests.stream()
                                .filter((request -> request.getEvent().equals(dto.getId())))
                                .count()
                ))
                .peek(dto -> eventRating.getOrDefault(dto.getId(), 0.0))
                .toList();
    }

    private InteractionsCountRequestProto getInteractionsRequest(List<Long> eventId) {
        return InteractionsCountRequestProto.newBuilder().addAllEventId(eventId).build();
    }
}
