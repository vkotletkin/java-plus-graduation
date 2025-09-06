package ru.practicum.application.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.request.EventRequestDto;
import ru.practicum.application.request.model.EventRequest;
import ru.practicum.util.JsonFormatPattern;

import java.time.format.DateTimeFormatter;
import java.util.List;



@Component
@AllArgsConstructor
public class EventRequestMapper {

    public EventRequestDto mapRequest(EventRequest request) {
        EventRequestDto dto = new EventRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester());
        dto.setEvent(request.getEvent());
        dto.setCreated(request.getCreated().format(DateTimeFormatter.ofPattern(JsonFormatPattern.TIME_PATTERN)));
        dto.setStatus(request.getStatus());
        return dto;
    }

    public EventRequestDto mapRequestWithConfirmedAndRejected(List<EventRequestDto> confirmedRequests,
                                                              List<EventRequestDto> rejectedRequests) {
        EventRequestDto result = new EventRequestDto();
        result.setConfirmedRequests(confirmedRequests);
        result.setRejectedRequests(rejectedRequests);
        return result;
    }
}
