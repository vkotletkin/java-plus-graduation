package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.util.JsonFormatPattern;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Requests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String application;

    @NotNull
    @Column(nullable = false)
    String uri;

    @Column(nullable = false)
    String ip;

    @DateTimeFormat(pattern = JsonFormatPattern.TIME_PATTERN)
    @Column(nullable = false)
    LocalDateTime moment;
}
