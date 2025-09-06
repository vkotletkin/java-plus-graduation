package ru.practicum.application.event.controller;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.application.event.service.InnerEventService;
import ru.practicum.api.event.InnerEventApi;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InnerEventController implements InnerEventApi {

    final InnerEventService innerEventService;

    @Override
    public EventFullDto getInnerEventById(Long eventId) throws NotFoundException {
        return innerEventService.getEventById(eventId);
    }

    @Override
    public boolean existsById(Long eventId) {
        return innerEventService.existsById(eventId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return innerEventService.existsByCategoryId(categoryId);
    }

    @Override
    public List<EventShortDto> getShortByIds(List<Long> ids) {
        return innerEventService.getShortByIds(ids);
    }
}
