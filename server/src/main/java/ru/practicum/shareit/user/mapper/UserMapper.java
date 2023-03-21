package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserMapper {
    User toNewEntity(UserDto dto);

    User toEntity(UserDto dto, Long id);

    UserDto toDto(User user);
}
