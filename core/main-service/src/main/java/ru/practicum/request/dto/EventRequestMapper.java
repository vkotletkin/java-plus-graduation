package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.client.util.JsonFormatPattern;
import ru.practicum.request.model.EventRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Component
@AllArgsConstructor
public class EventRequestMapper {
    public EventRequestDto mapRequest(EventRequest request) {
        EventRequestDto dto = new EventRequestDto();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setEvent(request.getEvent().getId());
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
