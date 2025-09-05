package ru.practicum.api.event;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;
import ru.practicum.request.event.UpdateEventAdminRequest;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;
import java.util.List;

@Validated
public interface AdminEventApi {

    String ADMIN_EVENTS_PATH = "/admin/events";

    @GetMapping(ADMIN_EVENTS_PATH)
    List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @DateTimeFormat(pattern = JsonFormatPattern.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = JsonFormatPattern.TIME_PATTERN) @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) throws ValidationException;

    @PatchMapping(ADMIN_EVENTS_PATH + "/{event-id}")
    EventFullDto updateEvent(@PathVariable(name = "event-id") Long eventId,
                             @Valid @RequestBody UpdateEventAdminRequest event)
            throws ValidationException, ConflictException, WrongDataException, NotFoundException;
}
