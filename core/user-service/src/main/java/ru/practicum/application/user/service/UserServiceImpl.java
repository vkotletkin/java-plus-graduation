package ru.practicum.application.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.application.user.mapper.UserMapper;
import ru.practicum.application.user.model.User;
import ru.practicum.application.user.repository.UserRepository;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto newUserDto) throws ConflictException {

        if (userRepository.existsByName(newUserDto.getName())) {
            throw new ConflictException("Пользователь с именем: {0} уже существует", newUserDto.getName());
        }

        User savedUser = userRepository.save(UserMapper.toModel(newUserDto));

        return UserMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long userId) throws NotFoundException {

        User user = userRepository.findById(userId).orElseThrow(
                notFoundException("Пользователь с идентификатором: {0} не найден", userId));

        return UserMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIdList(List<Long> ids, Pageable page) {

        List<User> users = (ids == null || ids.isEmpty()) ?
                userRepository.findAll(page).getContent() :
                userRepository.findAllByIdsPageable(ids, page);

        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existById(Long userId) {
        return userRepository.existsById(userId);
    }
}
