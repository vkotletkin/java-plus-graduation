package ru.practicum.application.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.service.EventService;
import ru.practicum.client.CategoryFeignClient;
import ru.practicum.client.RequestFeignClient;
import ru.practicum.client.StatsClient;
import ru.practicum.client.UserFeignClient;
import ru.practicum.client.util.JsonFormatPattern;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

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
    private final StatsClient statsClient;

    private final EventRepository eventRepository;

    @Override
    public EventFullDto getEventById(Long eventId, String uri, String ip) throws NotFoundException {

        statsClient.save(new StatsRequestDto("main-server",
                uri,
                ip,
                LocalDateTime.now()));

        Event event = eventRepository.findById(eventId).orElseThrow(notFoundException(NOT_FOUND_EVENT_MESSAGE, eventId));

        if (!event.getState().equals(EventState.PUBLISHED) && !uri.toLowerCase().contains("admin")) {
            throw new NotFoundException("Такого события не существует");
        }

        var confirmed = requestClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));

        EventFullDto eventFullDto = EventMapper.mapEventToFullDto(
                event,
                confirmed,
                categoryClient.getCategoryById(event.getCategory()),
                userClient.getById(event.getInitiator())
        );

        List<String> urls = Collections.singletonList(uri);
        LocalDateTime start = LocalDateTime.parse(eventFullDto.getCreatedOn(), DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
        LocalDateTime end = LocalDateTime.now();

        eventFullDto.setViews(statsClient.getStats(start, end, urls, true).size());

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

        List<Event> events;
        LocalDateTime startDate;
        LocalDateTime endDate;

        boolean sortDate = sort.equals("EVENT_DATE");

        if (sortDate) {
            if (rangeStart == null && rangeEnd == null && categories != null) {

                events = eventRepository.findAllByCategoryIdPageable(categories, EventState.PUBLISHED,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER)));
            } else {
                startDate = (rangeStart == null) ? LocalDateTime.now() :
                        LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));

                if (text == null) {

                    text = "";
                }

                if (rangeEnd == null) {
                    events = eventRepository.findEventsByText("%" + text.toLowerCase() + "%", EventState.PUBLISHED,
                            PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER)));
                } else {
                    endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
                    if (startDate.isAfter(endDate)) {
                        throw new ValidationException(DATE_AND_TIME_VALIDATION_MESSAGE);
                    }
                    events = eventRepository.findAllByTextAndDateRange("%" + text.toLowerCase() + "%",
                            startDate,
                            endDate,
                            EventState.PUBLISHED,
                            PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, PAGE_REQUEST_SORTING_PARAMETER)));

                }
            }
        } else {
            startDate = (rangeStart == null) ? LocalDateTime.now() :
                    LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));

            if (rangeEnd == null) {
                endDate = null;
            } else {
                endDate = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
            }
            if (rangeStart != null && rangeEnd != null && startDate.isAfter(endDate)) {
                throw new ValidationException(DATE_AND_TIME_VALIDATION_MESSAGE);
            }

            events = eventRepository.findEventList(text, categories, paid, startDate, endDate, EventState.PUBLISHED);
        }

        statsClient.save(new StatsRequestDto("main-server",
                uri,
                ip,
                LocalDateTime.now()));

        if (!sortDate) {
            List<EventShortDto> shortEventDtos = createShortEventDtos(events);
            shortEventDtos.sort(Comparator.comparing(EventShortDto::getViews));
            shortEventDtos = shortEventDtos.subList(from, Math.min(from + size, shortEventDtos.size()));
            return shortEventDtos;
        }

        return createShortEventDtos(events);
    }

    List<EventShortDto> createShortEventDtos(List<Event> events) {

        HashMap<Long, Integer> eventIdsWithViewsCounter = new HashMap<>();

        LocalDateTime startTime = events.getFirst().getCreatedOn();
        ArrayList<String> uris = new ArrayList<>();
        for (Event event : events) {
            uris.add("/events/" + event.getId().toString());
            if (startTime.isAfter(event.getCreatedOn())) {
                startTime = event.getCreatedOn();
            }
        }

        var viewsCounter = statsClient.getStats(startTime, LocalDateTime.now(), uris, true);

        for (var statsDto : viewsCounter) {
            String[] split = statsDto.getUri().split("/");
            eventIdsWithViewsCounter.put(Long.parseLong(split[2]), Math.toIntExact(statsDto.getHits()));
        }

        List<EventRequestDto> requests = requestClient.findByEventIds(new ArrayList<>(eventIdsWithViewsCounter.keySet()));
        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();

        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());

        Map<Long, UserDto> users = userClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        Map<Long, CategoryDto> categories = categoryClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return events.stream()
                .map(e -> EventMapper.mapEventToShortDto(e, categories.get(e.getCategory()), users.get(e.getInitiator())))
                .peek(dto -> dto.setConfirmedRequests(
                        requests.stream()
                                .filter((request -> request.getEvent().equals(dto.getId())))
                                .count()
                ))
                .peek(dto -> dto.setViews(eventIdsWithViewsCounter.get(dto.getId())))
                .toList();
    }
}
