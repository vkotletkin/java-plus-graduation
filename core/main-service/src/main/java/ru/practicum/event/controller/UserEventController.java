package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.UserEventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;

import java.util.List;

@RestController
@RequestMapping("/users/{user-id}/events")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEventController {

    final UserEventService eventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable(name = "user-id") Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer count) throws NotFoundException {
        return eventService.getUserEvents(userId, from, count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable(name = "user-id") Long userId,
                                 @Valid @RequestBody NewEventDto event) throws ValidationException, WrongDataException, NotFoundException {
        return eventService.addEvent(userId, event);
    }

    @GetMapping("/{event-id}")
    public EventFullDto getEventById(@PathVariable(name = "user-id") Long userId,
                                     @PathVariable(name = "event-id") Long eventId) throws ValidationException, NotFoundException {
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/{event-id}")
    public EventFullDto updateEvent(@PathVariable(name = "user-id") Long userId,
                                    @PathVariable(name = "event-id") Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest event) throws ValidationException, ConflictException, WrongDataException, NotFoundException {
        return eventService.updateEvent(userId, eventId, event);
    }
}
