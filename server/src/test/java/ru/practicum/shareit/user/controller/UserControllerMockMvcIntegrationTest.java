package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
class UserControllerMockMvcIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void find_whenInvoke_thenReturnOk() {
        long userId = 1L;

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).findUser(userId);
    }

    @SneakyThrows
    @Test
    void getAll_whenInvoke_thenReturnOkAndReturnList() {
        UserDto userDto1 = new UserDto();
        UserDto userDto2 = new UserDto();
        List<UserDto> listDto = new ArrayList<>(List.of(userDto1, userDto2));
        when(userService.getAll()).thenReturn(listDto);

        String result = mockMvc.perform(get("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(listDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(listDto), result);

    }

    @SneakyThrows
    @Test
    void create_whenInvokeWithNewDto_thenReturnDto() {
        UserDto userDto = new UserDto();
        when(userService.createUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void amend_whenInvokeWithUpdatedDto_thenReturnDto() {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        when(userService.updateUser(userDto, userId)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{id}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();;

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenReturnOk() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}