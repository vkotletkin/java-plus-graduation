package ru.practicum.application.request.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.application.request.service.EventRequestService;
import ru.practicum.api.request.RequestApi;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestController implements RequestApi {

    final EventRequestService requestService;

    @Override
    public EventRequestDto addEventRequest(Long userId, Long eventId) throws ConflictException, NotFoundException {
        return requestService.addRequest(userId, eventId);
    }

    @Override
    public List<EventRequestDto> getUserRequests(Long userId) throws NotFoundException {
        return requestService.getUserRequests(userId);
    }

    @Override
    public List<EventRequestDto> getRequestsByEventId(Long userId, Long eventId)
            throws ValidationException, NotFoundException {
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @Override
    public EventRequestDto updateRequest(Long userId, Long eventId, EventRequestDto request)
            throws ValidationException, ConflictException, NotFoundException {
        return requestService.updateRequest(userId, eventId, request);
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) throws ValidationException, NotFoundException {
        return requestService.cancelRequest(userId, requestId);
    }
}
