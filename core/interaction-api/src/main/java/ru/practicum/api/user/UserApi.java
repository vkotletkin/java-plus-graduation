package ru.practicum.api.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;

import java.util.List;

@Validated
public interface UserApi {

    String ADMIN_USERS_PATH = "/admin/users";

    @PostMapping(ADMIN_USERS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto newUser) throws ConflictException;

    @GetMapping(ADMIN_USERS_PATH)
    List<UserDto> getUsersList(@RequestParam(required = false) List<Long> ids,
                               @RequestParam(defaultValue = "0") Integer from,
                               @RequestParam(defaultValue = "10") Integer size);

    @DeleteMapping(ADMIN_USERS_PATH + "/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable(name = "user-id") Long userId);
}
