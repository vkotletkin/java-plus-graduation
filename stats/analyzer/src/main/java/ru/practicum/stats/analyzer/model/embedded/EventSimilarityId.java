package ru.practicum.stats.analyzer.model.embedded;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarityId implements Serializable {
    Long first;
    Long second;
}
