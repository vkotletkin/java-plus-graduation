package ru.practicum.api.event;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Validated
public interface InternalEventApi {

    String INNER_EVENT_PATH = "/internal/event";

    @GetMapping(INNER_EVENT_PATH + "/short/ids")
    List<EventShortDto> getShortByIds(@RequestParam List<Long> ids);

    @GetMapping(INNER_EVENT_PATH + "/{event-id}")
    EventFullDto getInnerEventById(@PathVariable(name = "event-id") Long eventId) throws NotFoundException;

    @GetMapping(INNER_EVENT_PATH + "/{event-id}/exist")
    boolean existsById(@PathVariable(name = "event-id") Long eventId);

    @GetMapping(INNER_EVENT_PATH + "/category/{category-id}/exist")
    boolean existsByCategoryId(@PathVariable(name = "category-id") Long categoryId);
}
