package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void find_whenInvoked_thenUserDtoFounded() {
        Long userId = 1L;
        UserDto oldDto = new UserDto();
        Mockito.when(userService.findUser(userId)).thenReturn(oldDto);

        UserDto newDto = userController.find(userId);

        assertEquals(oldDto, newDto);
    }

    @Test
    void getAll() {
        List<UserDto> oldDtos = List.of(new UserDto());
        Mockito.when(userService.getAll()).thenReturn(oldDtos);

        List<UserDto> newDtos = userController.getAll();

        assertEquals(oldDtos, newDtos);
    }
}