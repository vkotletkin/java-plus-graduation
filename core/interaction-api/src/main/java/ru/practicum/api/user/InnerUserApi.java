package ru.practicum.api.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;

@Validated
public interface InnerUserApi {

    String INNER_USER_BY_ID_PATH = "/internal/user/{user-id}";

    @GetMapping(INNER_USER_BY_ID_PATH)
    UserDto getById(@PathVariable(name = "user-id") Long userId) throws NotFoundException;

    @GetMapping(INNER_USER_BY_ID_PATH + "/exist")
    boolean existsById(@PathVariable(name = "user-id") Long userId);
}
