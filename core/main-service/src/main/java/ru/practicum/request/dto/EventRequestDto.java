package ru.practicum.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDto {

    Long id;

    String status;

    Long event;

    Long requester;

    String created;

    List<EventRequestDto> confirmedRequests;

    List<EventRequestDto> rejectedRequests;
}
