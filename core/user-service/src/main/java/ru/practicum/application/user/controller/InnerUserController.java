package ru.practicum.application.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.application.user.service.UserService;
import ru.practicum.api.user.InnerUserApi;

@RestController
@RequiredArgsConstructor
public class InnerUserController implements InnerUserApi {

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
