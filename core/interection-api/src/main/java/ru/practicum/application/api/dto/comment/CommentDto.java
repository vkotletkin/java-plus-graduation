package ru.practicum.application.api.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

import static ru.practicum.application.api.util.JsonFormatPattern.JSON_FORMAT_PATTERN_FOR_TIME;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CommentDto {
    Long id;
    Long userId;
    Long eventId;
    @JsonProperty("isInitiator")
    boolean isInitiator;
    @Size(min = 1, max = 5000)
    @NotBlank
    String content;
    @JsonFormat(pattern = JSON_FORMAT_PATTERN_FOR_TIME)
    LocalDateTime created;
}
