package ru.practicum.application.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.application.event.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
}
