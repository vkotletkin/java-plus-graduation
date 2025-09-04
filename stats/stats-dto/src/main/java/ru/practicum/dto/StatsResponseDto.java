package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsResponseDto {
    @NotBlank(message = "Идентификатор сервиса для которого записывается информация не должен быть пустым.")
    String app;

    @NotBlank(message = "URI для которого был осуществлен запрос не должен быть пустым.")
    String uri;

    Long hits;
}
