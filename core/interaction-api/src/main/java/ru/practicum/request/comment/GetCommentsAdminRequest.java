package ru.practicum.request.comment;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GetCommentsAdminRequest {
    Long eventId;
    Integer from;
    Integer size;
}
