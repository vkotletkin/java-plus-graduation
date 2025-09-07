package ru.practicum.api.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

@Validated
public interface PublicEventApi {
    String EVENTS_PATH = "/events";

    @GetMapping(EVENTS_PATH)
    List<EventShortDto> getFilteredEvents(@RequestParam(required = false) String text,
                                          @RequestParam(required = false) List<Long> categories,
                                          @RequestParam(required = false) Boolean paid,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(defaultValue = "false") Boolean available,
                                          @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                          @Positive @RequestParam(defaultValue = "10") Integer count,
                                          HttpServletRequest request) throws ValidationException;

    @GetMapping(EVENTS_PATH + "/{id}")
    EventFullDto getEventById(@PathVariable(name = "id") Long id, HttpServletRequest request) throws NotFoundException;
}
