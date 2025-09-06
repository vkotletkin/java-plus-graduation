package ru.practicum.application.request.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.request.model.EventRequest;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.util.JsonFormatPattern;

import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EventRequestMapper {

    public static EventRequestDto mapRequest(EventRequest request) {
        return EventRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester())
                .event(request.getEvent())
                .created(request.getCreated().format(DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN)))
                .status(request.getStatus())
                .build();
    }

    public static EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                                     List<EventRequestDto> rejectedRequests) {
        return EventRequestDto.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }
}
