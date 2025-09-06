package ru.practicum.application.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.application.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByName(String name);

    @Query("SELECT u FROM User u " +
            "WHERE(u.id in :ids)")
    List<User> findAllByIdsPageable(List<Long> ids, Pageable page);
}
