package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.AdminEventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.WrongDataException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final AdminEventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME) @RequestParam(required = false) LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) throws ValidationException {
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{event-id}")
    public EventFullDto updateEvent(@PathVariable(name = "event-id") Long eventId,
                                    @Valid @RequestBody UpdateEventAdminRequest event) throws ValidationException, ConflictException, WrongDataException, NotFoundException {
        return eventService.updateEvent(eventId, event);
    }
}
