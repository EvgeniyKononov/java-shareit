package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerMockMvcIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @SneakyThrows
    @Test
    void create_whenInvokeWithNewDto_thenReturnDto() {
        Long userId = 1L;
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("description");
        when(requestService.createRequest(userId, requestDto)).thenReturn(requestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).createRequest(userId, requestDto);
        assertEquals(objectMapper.writeValueAsString(requestDto), result);
    }

    @SneakyThrows
    @Test
    void findAllByUserId_whenInvoke_thenReturnOk() {
        Long userId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).findAllByUserId(userId);
    }

    @SneakyThrows
    @Test
    void findAll_whenInvoke_thenReturnOkAndReturnList() {
        Long userId = 1L;
        ItemRequestDto requestDto1 = new ItemRequestDto();
        ItemRequestDto requestDto2 = new ItemRequestDto();
        List<ItemRequestDto> listDto = new ArrayList<>(List.of(requestDto1, requestDto2));
        when(requestService.findAll(userId, PageRequest.of(0, 10))).thenReturn(listDto);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(listDto), result);
    }

    @SneakyThrows
    @Test
    void findById_whenInvoke_thenReturnOk() {
        Long userId = 1L;
        Long requestId = 2L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestService).findById(userId, requestId);
    }
}