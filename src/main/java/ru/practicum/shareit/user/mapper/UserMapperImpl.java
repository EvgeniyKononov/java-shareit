package ru.practicum.shareit.user.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserMapperImpl implements UserMapper {
    private final JpaUserRepository userRepository;

    @Autowired
    public UserMapperImpl(JpaUserRepository userRepository) {
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
        Optional<User> userOptional = userRepository.findById(id);
        User user = new User();
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
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
