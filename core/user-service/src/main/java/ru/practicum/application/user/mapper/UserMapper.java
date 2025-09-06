package ru.practicum.application.user.mapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import ru.practicum.application.user.model.User;
import ru.practicum.dto.user.UserDto;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toModel(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
