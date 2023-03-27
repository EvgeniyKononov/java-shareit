package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerMockMvcIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private Long userId;
    private Long itemId;
    private ItemDto dto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 1L;
        dto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

    }

    @SneakyThrows
    @Test
    void findItemById_whenInvoke_thenReturnDtoAndStatusOk() {
        when(itemService.findItem(userId, itemId)).thenReturn(dto);

        String result = mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), result);
    }

    @SneakyThrows
    @Test
    void findItemsByOwner_whenInvoke_thenReturnListDtoAndStatusOk() {
        ItemDto dto2 = new ItemDto();
        List<ItemDto> itemDtoList = new ArrayList<>(List.of(dto, dto2));
        when(itemService.findItemsByOwner(userId, PageRequest.of(0, 10))).thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @SneakyThrows
    @Test
    void findItemsByText_whenInvoke_thenReturnListDtoAndStatusOk() {
        ItemDto dto2 = new ItemDto();
        List<ItemDto> itemDtoList = new ArrayList<>(List.of(dto, dto2));
        String text = "text";
        when(itemService.findItemsByText(text, PageRequest.of(0, 10))).thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @SneakyThrows
    @Test
    void create_whenInvokeWithNewDto_thenReturnDtoAndStatusOk() {
        when(itemService.createItem(userId, dto)).thenReturn(dto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), result);
    }

    @SneakyThrows
    @Test
    void amend_whenInvokeWithNewDto_thenReturnDtoAndStatusOk() {
        when(itemService.updateItem(userId, itemId, dto)).thenReturn(dto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), result);
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenStatusOk() {
        mockMvc.perform(delete("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, only()).deleteItem(userId, itemId);
    }

    @SneakyThrows
    @Test
    void createComment_whenInvokeWithNewDto_thenReturnDtoAndStatusOk() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }
}