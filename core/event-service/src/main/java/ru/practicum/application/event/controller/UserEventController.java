package ru.practicum.application.event.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.UserEventApi;
import ru.practicum.application.event.service.UserEventService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.request.event.UpdateEventUserRequest;


import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEventController implements UserEventApi {

    final UserEventService eventService;

    @Override
    public List<EventShortDto> getUserEvents(
            Long userId,
            Integer from,
            Integer count
    ) throws NotFoundException {
        return eventService.getUserEvents(userId, from, count);
    }

    @Override
    public EventFullDto addEvent(
            Long userId,
            NewEventDto event
    ) throws ValidationException, WrongDataException, NotFoundException, ConflictException {
        return eventService.addEvent(userId, event);
    }

    @Override
    public EventFullDto getUserEventById(
            Long userId,
            Long eventId
    ) throws ValidationException, NotFoundException {
        return eventService.getEventById(userId, eventId);
    }

    @Override
    public EventFullDto updateUserEvent(
            Long userId,
            Long eventId,
            UpdateEventUserRequest event
    ) throws ValidationException, ConflictException, WrongDataException, NotFoundException {
        return eventService.updateEvent(userId, eventId, event);
    }
}
