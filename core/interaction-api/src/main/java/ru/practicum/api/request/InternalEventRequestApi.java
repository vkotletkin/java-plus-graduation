package ru.practicum.api.request;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.request.EventRequestDto;

import java.util.List;

@Validated
public interface InternalEventRequestApi {

    String INTERNAL_REQUEST_PATH = "/internal/request";

    @GetMapping(INTERNAL_REQUEST_PATH + "/events")
    List<EventRequestDto> findByEventIds(@RequestParam List<Long> id);

    @GetMapping(INTERNAL_REQUEST_PATH + "/events/{status}")
    List<EventRequestDto> getByEventAndStatus(@RequestParam List<Long> eventId, @PathVariable(name = "status") String status);

    @GetMapping(INTERNAL_REQUEST_PATH + "/{event-id}/status/count")
    Long countByEventAndStatuses(@PathVariable(name = "event-id") Long eventId, @RequestParam List<String> statuses);

    @GetMapping("/internal/{user-id}/take/{event-id}")
    boolean isUserTakePart(@PathVariable(name = "user-id") Long userId, @PathVariable(name = "event-id") Long eventId);
}
