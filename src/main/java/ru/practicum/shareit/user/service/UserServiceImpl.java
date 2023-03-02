package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.exception.DuplicateUserException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.constant.Constant.EMAIL_ALREADY_REGISTERED_MSG;
import static ru.practicum.shareit.constant.Constant.NOT_FOUND_USER_ID;

@Service
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(JpaUserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = userMapper.toNewEntity(userDto);
        return userMapper.toDto(userRepository.save(newUser));
    }

    @Override
    public UserDto findUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userMapper.toDto(user.get());
        } else throw new NotFoundItemException(NOT_FOUND_USER_ID);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> usersDto = new ArrayList<>();
        List<User> users = userRepository.findAll();
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
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void checkEmailDuplicate(User checkedUser) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals(checkedUser.getEmail()) && user.getId() != checkedUser.getId()) {
                throw new DuplicateUserException(EMAIL_ALREADY_REGISTERED_MSG);
            }
        }
    }
}
