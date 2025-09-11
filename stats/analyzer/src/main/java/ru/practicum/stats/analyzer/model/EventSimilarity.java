package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.stats.analyzer.model.embedded.EventSimilarityId;

@Entity
@Table(name = "event_similarities")
@IdClass(EventSimilarityId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventSimilarity {

    @Id
    @Column(name = "first_event")
    Long first;

    @Id
    @Column(name = "second_event")
    Long second;

    Double score;
}
