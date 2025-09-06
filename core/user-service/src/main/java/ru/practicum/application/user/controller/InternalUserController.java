package ru.practicum.application.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.application.user.service.UserService;
import ru.practicum.api.user.InternalUserApi;

@RestController
@RequiredArgsConstructor
public class InternalUserController implements InternalUserApi {

    private final UserService userService;

    @Override
    public UserDto getById(Long userId) throws NotFoundException {
        return userService.getUserById(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return userService.existById(userId);
    }
}
