package ru.practicum.api.request;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

@Validated
public interface RequestApi {

    String USERS_BY_ID_REQUESTS_PATH = "/users/{user-id}/requests";
    String USERS_BY_ID_EVENTS_BY_ID_REQUESTS_PATH = "/users/{user-id}/events/{event-id}/requests";

    @PostMapping(USERS_BY_ID_REQUESTS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    EventRequestDto addEventRequest(@PathVariable(name = "user-id") Long userId,
                                    @RequestParam Long eventId
    ) throws ConflictException, NotFoundException;

    @GetMapping(USERS_BY_ID_REQUESTS_PATH)
    List<EventRequestDto> getUserRequests(@PathVariable(name = "user-id") Long userId) throws NotFoundException;

    @GetMapping(USERS_BY_ID_EVENTS_BY_ID_REQUESTS_PATH)
    List<EventRequestDto> getRequestsByEventId(@PathVariable(name = "user-id") Long userId,
                                               @PathVariable(name = "event-id") Long eventId) throws ValidationException, NotFoundException;

    @PatchMapping(USERS_BY_ID_EVENTS_BY_ID_REQUESTS_PATH)
    EventRequestDto updateRequest(@PathVariable(name = "user-id") Long userId,
                                  @PathVariable(name = "event-id") Long eventId,
                                  @RequestBody EventRequestDto request) throws ValidationException, ConflictException, NotFoundException;

    @PatchMapping(USERS_BY_ID_REQUESTS_PATH + "/{request-id}/cancel")
    EventRequestDto cancelRequest(@PathVariable(name = "user-id") Long userId,
                                  @PathVariable(name = "request-id") Long requestId) throws ValidationException, NotFoundException;
}
