package ru.practicum.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EventRequestDto {
    Long id;
    String status;
    Long event;
    Long requester;
    String created;
    List<EventRequestDto> confirmedRequests;
    List<EventRequestDto> rejectedRequests;
}
