package ru.practicum.application.event.service;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

public interface InnerEventService {
    EventFullDto getEventById(Long eventId) throws NotFoundException;

    boolean existsById(Long eventId);

    boolean existsByCategoryId(Long categoryId);

    List<EventShortDto> getShortByIds(List<Long> ids);
}
