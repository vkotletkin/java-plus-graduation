package ru.practicum.request.event;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.event.LocationDto;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UpdateEventAdminRequest {

    Long category;

    @Size(min = 3, max = 120)
    String title;

    @Size(min = 20, max = 2000)
    String annotation;

    @Size(min = 20, max = 7000)
    String description;

    String stateAction;
    String eventDate;

    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
}
