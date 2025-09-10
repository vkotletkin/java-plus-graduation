package ru.practicum.application.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.event.PublicEventApi;
import ru.practicum.application.event.service.EventService;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicEventController implements PublicEventApi {

    private final EventService eventService;

    @Override
    public EventFullDto getEventById(Long id, Long userId,
                                     HttpServletRequest request) throws NotFoundException {
        return eventService.getEventById(id, request.getRequestURI(), request.getRemoteAddr());
    }

    @Override
    public List<EventShortDto> getFilteredEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean available,
                                                 String sort,
                                                 Integer from,
                                                 Integer count,
                                                 HttpServletRequest request
    ) throws ValidationException {
        return eventService.getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, available, sort, from, count,
                request.getRequestURI(), request.getRemoteAddr());
    }


    @Override
    public List<EventFullDto> getRecommendations(Long userId) {
        return eventService.getRecommendations(userId);
    }

    @Override
    public void likeEvent(Long eventId, Long userId) throws ValidationException {
        eventService.likeEvent(eventId, userId);
    }
}