package ru.practicum.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class LocationDto {
    Long id;
    Float lat;
    Float lon;
}
