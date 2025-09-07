package ru.practicum.request.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class UpdateCompilationRequest {

    @Size(max = 50)
    String title;

    List<Long> events;
    Boolean pinned;
}