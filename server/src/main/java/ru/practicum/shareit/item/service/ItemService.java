package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto findItem(Long userId, Long itemId);

    List<ItemDto> findItemsByOwner(Long userId, PageRequest of);

    List<ItemDto> findItemsByText(String text, PageRequest of);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long id);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
