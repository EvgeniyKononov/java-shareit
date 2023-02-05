package ru.practicum.shareit.user.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.Objects;

@Component
public class UserMapperImpl implements UserMapper {
    private final UserRepository userRepository;

    @Autowired
    public UserMapperImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User toNewEntity(UserDto dto) {
        return new User(
                dto.getId(),
                dto.getName(),
                dto.getEmail()
        );
    }

    @Override
    public User toEntity(UserDto dto, Long id) {
        User user = userRepository.find(id);
        String name = user.getName();
        String email = user.getEmail();
        if (Objects.nonNull(dto.getName())) {
            name = dto.getName();
        }
        if (Objects.nonNull(dto.getEmail())) {
            email = dto.getEmail();
        }
        return new User(
                id,
                name,
                email
        );
    }

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
