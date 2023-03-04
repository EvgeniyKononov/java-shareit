package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto findUser(Long id);

    List<UserDto> getAll();

    UserDto updateUser(UserDto userDto, Long id);

    void deleteUser(Long id);
}
