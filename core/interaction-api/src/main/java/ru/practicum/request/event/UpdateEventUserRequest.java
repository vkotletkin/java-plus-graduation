package ru.practicum.request.event;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.LocationDto;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UpdateEventUserRequest {

    Long category;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 7000)
    String description;

    String stateAction;
    String eventDate;
    LocationDto location;
    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;
}
