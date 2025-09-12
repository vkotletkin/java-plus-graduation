package ru.practicum.application.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.event.mapper.EventMapper;
import ru.practicum.application.event.model.Event;
import ru.practicum.application.event.repository.EventRepository;
import ru.practicum.application.event.service.InnerEventService;
import ru.practicum.stats.client.CategoryFeignClient;
import ru.practicum.stats.client.UserFeignClient;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class InnerEventServiceImpl implements InnerEventService {

    private static final String NOT_FOUND_EVENT_MESSAGE = "Событие с идентификатором: {0} - не найдено";

    private final EventRepository eventRepository;

    private final UserFeignClient userFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @Override
    public EventFullDto getEventById(Long eventId) throws NotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(
                notFoundException(NOT_FOUND_EVENT_MESSAGE, eventId));

        return EventMapper.mapEventToFullDto(event, null,
                categoryFeignClient.getCategoryById(event.getCategory()),
                userFeignClient.getById(event.getInitiator()));
    }

    @Override
    public boolean existsById(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return eventRepository.existsByCategory(categoryId);
    }

    @Override
    public List<EventShortDto> getShortByIds(List<Long> ids) {

        List<Event> events = eventRepository.findAllById(ids);
        List<Long> usersIds = events.stream().map(Event::getInitiator).toList();

        Set<Long> categoriesIds = events.stream().map(Event::getCategory).collect(Collectors.toSet());

        Map<Long, UserDto> users = userFeignClient.getUsersList(usersIds, 0, Math.max(events.size(), 1)).stream()
                .collect(Collectors.toMap(UserDto::getId, userDto -> userDto));

        Map<Long, CategoryDto> categories = categoryFeignClient.getCategoriesByIds(categoriesIds).stream()
                .collect(Collectors.toMap(CategoryDto::getId, categoryDto -> categoryDto));

        return events.stream().map(
                e -> EventMapper.mapEventToShortDto(e, categories.get(e.getCategory()), users.get(e.getInitiator()))).toList();
    }
}
