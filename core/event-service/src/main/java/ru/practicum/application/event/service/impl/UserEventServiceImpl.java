package ru.practicum.application.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.repository.LocationRepository;
import ru.practicum.application.event.service.UserEventService;
import ru.practicum.client.CategoryFeignClient;
import ru.practicum.client.RequestFeignClient;
import ru.practicum.client.StatsClient;
import ru.practicum.client.UserFeignClient;
import ru.practicum.client.util.JsonFormatPattern;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.enums.StateAction;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.request.event.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class UserEventServiceImpl implements UserEventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    private final UserFeignClient userFeignClient;
    private final CategoryFeignClient categoryFeignClient;
    private final RequestFeignClient requestFeignClient;
    private final StatsClient statsClient;

    private static void validationEventDate(Event event) throws ValidationException, WrongDataException {

        if (LocalDateTime.now().isAfter(event.getEventDate().minusHours(1))) {
            throw new ValidationException("До начала события меньше часа, изменение невозможно");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new WrongDataException("Событие уже завершилось");
        }
    }

    private static void validationEventInitiator(UserDto user, Event event) throws ValidationException {
        if (!user.getId().equals(event.getInitiator())) {
            throw new ValidationException("Пользователь {0} не инициатор события", event.getId());
        }
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) throws ValidationException, WrongDataException, NotFoundException, ConflictException {

        UserDto user = getUserById(userId);
        CategoryDto category = categoryFeignClient.getCategoryById(eventDto.getCategory());

        Event event = EventMapper.mapNewEventDtoToEvent(eventDto, category);

        locationRepository.save(event.getLocation());

        event.setInitiator(user.getId());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        validationEventDate(event);
        event = eventRepository.save(event);

        return EventMapper.mapEventToFullDto(event, 0L, category, user);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest eventDto) throws ConflictException, NotFoundException, ValidationException, WrongDataException {

        Event event = getEventById(eventId);
        UserDto user = getUserById(userId);
        validationEventInitiator(user, event);
        validationEventDate(event);

        if ((!StateAction.REJECT_EVENT.toString().equals(eventDto.getStateAction()) && event.getState().equals(EventState.PUBLISHED))) {
            throw new ConflictException("Отклонить опубликованное событие невозможно");
        }

        updateEventFromEventDto(event, eventDto);
        locationRepository.save(event.getLocation());
        eventRepository.save(event);

        Long confirmed = requestFeignClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));

        return getViewsCounter(EventMapper.mapEventToFullDto(event, confirmed,
                categoryFeignClient.getCategoryById(event.getCategory()), user));
    }

    private UserDto getUserById(Long userId) throws NotFoundException {
        return userFeignClient.getById(userId);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer count) throws NotFoundException {

        UserDto user = getUserById(userId);
        List<Event> allEvents = eventRepository.findAllByInitiator(user.getId(), PageRequest.of(from / count, count));

        Set<Long> categoriesIds = allEvents.stream().map(Event::getCategory).collect(Collectors.toSet());
        Map<Long, CategoryDto> categories = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, c -> c));

        return allEvents.stream()
                .map(e -> EventMapper.mapEventToShortDto(e, categories.get(e.getCategory()), user))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long userId, Long eventId) throws NotFoundException, ValidationException {

        UserDto user = getUserById(userId);
        Event event = getEventById(eventId);

        if (!user.getId().equals(event.getInitiator())) {
            throw new ValidationException("Пользователь " + userId + " не является инициатором события " + eventId);
        }

        Long confirmed = requestFeignClient.countByEventAndStatuses(event.getId(), List.of("CONFIRMED"));
        return getViewsCounter(EventMapper.mapEventToFullDto(event, confirmed,
                categoryFeignClient.getCategoryById(event.getCategory()), user));
    }

    private Event getEventById(Long eventId) throws NotFoundException {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие " + eventId + " не найдено"));
    }

    private void updateEventFromEventDto(Event event, UpdateEventUserRequest inpEventDto) throws NotFoundException, ValidationException {

        if (inpEventDto.getAnnotation() != null) {
            event.setAnnotation(inpEventDto.getAnnotation());
        }

        if (inpEventDto.getCategory() != null) {
            if (!categoryFeignClient.existById(inpEventDto.getCategory())) {
                throw new NotFoundException("Категория не найдена " + inpEventDto.getCategory());
            }
            event.setCategory(inpEventDto.getCategory());
        }
        if (inpEventDto.getDescription() != null) {
            event.setDescription(inpEventDto.getDescription());
        }

        if (inpEventDto.getEventDate() != null) {
            LocalDateTime updateEventDate = LocalDateTime.parse(inpEventDto.getEventDate(),
                    DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
            if (LocalDateTime.now().isAfter(updateEventDate)) {
                throw new ValidationException("Нельзя установить дату из прошлого.");
            }
            event.setEventDate(updateEventDate);
        }

        if (inpEventDto.getLocation() != null) {
            event.setLocation(EventMapper.mapDtoToLocation(inpEventDto.getLocation()));
        }

        if (inpEventDto.getPaid() != null) {
            event.setPaid(inpEventDto.getPaid());
        }

        if (inpEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(inpEventDto.getParticipantLimit());
        }

        if (inpEventDto.getRequestModeration() != null) {
            event.setRequestModeration(inpEventDto.getRequestModeration());
        }

        if (inpEventDto.getStateAction() != null) {
            switch (inpEventDto.getStateAction().toUpperCase()) {
                case "PUBLISH_EVENT":
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                default:
                    throw new ValidationException("Неверное состояние события, не удалось обновить");
            }
        }
    }

    private EventFullDto getViewsCounter(EventFullDto eventFullDto) {

        ArrayList<String> urls = new ArrayList<>(List.of("/events/" + eventFullDto.getId()));

        LocalDateTime start = LocalDateTime.parse(eventFullDto.getCreatedOn(), DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN));
        LocalDateTime end = LocalDateTime.now();

        Integer views = statsClient.getStats(start, end, urls, true).size();
        eventFullDto.setViews(views);
        return eventFullDto;
    }
}
