package ru.practicum.compilation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ResponseCompilationDto {
    Long id;
    String title;
    Boolean pinned;
    List<EventShortDto> events;
}
