package ru.practicum.application.request.api;

import org.springframework.web.bind.annotation.*;
import ru.practicum.application.api.dto.request.EventRequestDto;

import java.util.List;

public interface InnerEventRequestInterface {
    @GetMapping("/inner/request/{eventId}/status/count")
    Long countByEventAndStatuses(@PathVariable Long eventId, @RequestParam List<String> statuses);
    @GetMapping("/inner/request/events/{status}")
    List<EventRequestDto> getByEventAndStatus(@RequestParam List<Long> eventId, @PathVariable String status);

    @GetMapping("/inner/request/events")
    List<EventRequestDto> findByEventIds(@RequestParam List<Long> id);
}
