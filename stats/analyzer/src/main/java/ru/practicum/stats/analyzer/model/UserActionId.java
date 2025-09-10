package ru.practicum.stats.analyzer.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserActionId implements Serializable {
    private Long userId;
    private Long eventId;
}
