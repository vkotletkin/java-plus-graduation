package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;

@Data
@Builder
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Long id;

    Long userId;

    Long eventId;

    @JsonProperty("isInitiator")
    boolean isInitiator;

    @Size(min = 1, max = 5000)
    @NotBlank
    String content;

    @JsonFormat(pattern = JsonFormatPattern.TIME_PATTERN)
    LocalDateTime created;
}
