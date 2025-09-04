package ru.practicum.application.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.application.api.dto.user.UserDto;
import ru.practicum.application.user.model.User;

@UtilityClass
public class UserMapper {
    public User mapDtoToUser(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserDto mapUserToDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}
