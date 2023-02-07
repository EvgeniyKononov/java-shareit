package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.exception.DuplicateUserException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.toNewEntity(userDto);
        checkEmailDuplicate(newUser);
        return userMapper.toDto(userRepository.save(newUser));
    }

    @Override
    public UserDto findUser(Long id) {
        User user = userRepository.find(id);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.getAll();
        for (User user : users) {
            usersDto.add(userMapper.toDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = userMapper.toEntity(userDto, id);
        if (Objects.nonNull(userDto.getEmail())) {
            checkEmailDuplicate(user);
        }
        return userMapper.toDto(userRepository.amend(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(id);
    }

    private void checkEmailDuplicate(User checkedUser) {
        List<User> users = userRepository.getAll();
        for (User user : users) {
            if (user.getEmail().equals(checkedUser.getEmail())) {
                throw new DuplicateUserException("Пользователь с такой почтой уже зарегестрирован");
            }
        }
    }
}
