package ru.practicum.application.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.user.UserApi;
import ru.practicum.application.user.service.UserService;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public List<UserDto> getUsersList(List<Long> ids, Integer from, Integer size) {
        return userService.getUsersByIdList(ids, PageRequest.of(from, size));
    }

    @Override
    public UserDto addUser(UserDto newUser) throws ConflictException {
        return userService.addUser(newUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }
}