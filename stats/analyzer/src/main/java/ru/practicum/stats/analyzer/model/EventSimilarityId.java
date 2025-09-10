package ru.practicum.stats.analyzer.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventSimilarityId implements Serializable {
    private Long first;
    private Long second;
}
