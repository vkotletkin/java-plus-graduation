package ru.practicum.dto.event;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EventShortDto {

    Long id;
    String title;
    String annotation;
    Long confirmedRequests;
    CategoryDto category;
    UserDto initiator;

    @NotNull
    @JsonFormat(pattern = JsonFormatPattern.TIME_PATTERN)
    LocalDateTime eventDate;

    Boolean paid;
    Double rating;
}
