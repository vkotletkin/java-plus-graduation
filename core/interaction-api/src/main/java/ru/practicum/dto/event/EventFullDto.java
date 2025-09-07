package ru.practicum.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.enums.EventState;
import ru.practicum.dto.user.UserDto;

@Data
@Builder
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EventFullDto {

    Long id;

    @NotBlank
    @Size(min = 1, max = 128)
    String title;

    @NotBlank
    @Size(min = 1, max = 1024)
    String annotation;

    CategoryDto category;
    Boolean paid;
    String eventDate;
    UserDto initiator;

    @NotBlank
    @Size(min = 1, max = 1024)
    String description;

    Integer participantLimit;

    EventState state;

    String createdOn;

    LocationDto location;

    Boolean requestModeration;

    Long confirmedRequests;

    String publishedOn;

    Integer views;
}
