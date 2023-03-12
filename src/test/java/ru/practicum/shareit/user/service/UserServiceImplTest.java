package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateUserException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private Long userId;
    private User expectedUser;
    private User actualUser;
    private UserDto expectedDto;
    private UserDto actualDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        expectedUser = new User();
        actualUser = new User();
        expectedDto = new UserDto();
        actualDto = new UserDto();
    }

    @Test
    void createUser_whenInvoke_thenSavedUser() {
        when(userMapper.toNewEntity(expectedDto)).thenReturn(expectedUser);
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        when(userMapper.toDto(expectedUser)).thenReturn(expectedDto);

        actualDto = userService.createUser(expectedDto);

        assertEquals(expectedDto, actualDto);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void findUser_whenUserFound_thenReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toDto(expectedUser)).thenReturn(expectedDto);

        actualDto = userService.findUser(userId);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void findUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class,
                () -> userService.findUser(userId));
    }

    @Test
    void getAll_whenInvoke_thenReturnDtoList() {
        List<UserDto> expectedUsersDto = new ArrayList<>(List.of(expectedDto, actualDto));
        List<User> users = new ArrayList<>(List.of(expectedUser, actualUser));
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(expectedUser)).thenReturn(expectedDto);
        when(userMapper.toDto(actualUser)).thenReturn(actualDto);

        List<UserDto> actualUsersDto = userService.getAll();

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    void updateUser_whenInvoke_thenUpdatedUser() {
        when(userMapper.toEntity(expectedDto, userId)).thenReturn(expectedUser);
        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        when(userMapper.toDto(expectedUser)).thenReturn(expectedDto);

        actualDto = userService.updateUser(expectedDto, userId);

        assertEquals(expectedDto, actualDto);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void checkEmailDuplicate_whenEmailDuplicate_thenDuplicateUserExceptionThrown() {
        actualUser.setId(1L);
        actualUser.setEmail("email@email.ru");
        expectedUser.setId(2L);
        expectedUser.setEmail("email@email.ru");
        when(userMapper.toEntity(expectedDto, userId)).thenReturn(expectedUser);
        when(userRepository.findAll()).thenReturn(List.of(actualUser));
        List<User> users = new ArrayList<>(List.of(actualUser));
        when(userRepository.findAll()).thenReturn(users);

        assertThrows(DuplicateUserException.class,
                () -> userService.updateUser(expectedDto, 1L));
    }

    @Test
    void deleteUser_whenInvoke_thenUserDeleteFromRep() {

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}