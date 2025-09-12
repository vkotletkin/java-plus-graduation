package ru.practicum.stats.analyzer.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.stats.analyzer.model.embedded.UserActionId;

import java.time.Instant;

@Data
@Entity
@Table(name = "user_actions")
@IdClass(UserActionId.class)
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAction {

    @Id
    @Column(name = "user_id")
    Long userId;

    @Id
    @Column(name = "event_id")
    Long eventId;

    @Column(name = "user_score")
    Double score;

    @Column(name = "timestamp_action")
    Instant timestamp;
}
