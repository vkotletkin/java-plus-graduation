package ru.practicum.application.category.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@Builder
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false, length = 128)
    String name;
}
