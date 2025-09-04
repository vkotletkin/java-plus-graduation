package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsRequestDto {
    @NotBlank(message = "Идентификатор сервиса для которого записывается информация не должен быть пустым.")
    String app;
    @NotBlank(message = "URI для которого был осуществлен запрос не должен быть пустым.")
    String uri;
    @NotBlank(message = "IP-адрес пользователя, осуществившего запрос не можен быть пустым")
    String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("timestamp")
    LocalDateTime timestamp;
}