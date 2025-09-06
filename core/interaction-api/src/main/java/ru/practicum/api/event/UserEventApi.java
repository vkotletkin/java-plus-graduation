package ru.practicum.api.event;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.request.event.UpdateEventUserRequest;

import java.util.List;

public interface UserEventApi {

    String USERS_BY_ID_EVENTS_PATH = "/users/{user-id}/events";

    @GetMapping(USERS_BY_ID_EVENTS_PATH)
    List<EventShortDto> getUserEvents(@PathVariable(name = "user-id") Long userId,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer count) throws NotFoundException;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(USERS_BY_ID_EVENTS_PATH)
    EventFullDto addEvent(@PathVariable(name = "user-id") Long userId,
                          @Valid @RequestBody NewEventDto event)
            throws ValidationException, WrongDataException, NotFoundException, ConflictException;

    @GetMapping(USERS_BY_ID_EVENTS_PATH + "/{event-id}")
    EventFullDto getUserEventById(@PathVariable(name = "user-id") Long userId,
                                  @PathVariable(name = "event-id") Long eventId) throws ValidationException, NotFoundException;

    @PatchMapping(USERS_BY_ID_EVENTS_PATH + "/{event-id}")
    EventFullDto updateUserEvent(@PathVariable(name = "user-id") Long userId,
                                 @PathVariable(name = "event-id") Long eventId,
                                 @Valid @RequestBody UpdateEventUserRequest event) throws ValidationException, ConflictException, WrongDataException, NotFoundException;
}
