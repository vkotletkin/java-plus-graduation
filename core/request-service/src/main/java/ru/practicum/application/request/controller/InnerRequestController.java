package ru.practicum.application.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.application.request.service.EventRequestService;
import ru.practicum.api.request.InnerEventRequestApi;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InnerRequestController implements InnerEventRequestApi {

    final EventRequestService service;

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
