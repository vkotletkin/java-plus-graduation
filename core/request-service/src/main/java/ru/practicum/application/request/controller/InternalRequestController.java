package ru.practicum.application.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.request.InternalEventRequestApi;
import ru.practicum.application.request.service.EventRequestService;
import ru.practicum.dto.request.EventRequestDto;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class InternalRequestController implements InternalEventRequestApi {

    private final EventRequestService service;

    @Override
    public Long countByEventAndStatuses(Long eventId, List<String> statuses) {
        return service.countByEventAndStatuses(eventId, statuses);
    }

    @Override
    public List<EventRequestDto> getByEventAndStatus(List<Long> eventId, String status) {
        return service.getByEventAndStatus(eventId, status);
    }

    @Override
    public List<EventRequestDto> findByEventIds(List<Long> id) {
        return service.findByEventIds(id);
    }
}
