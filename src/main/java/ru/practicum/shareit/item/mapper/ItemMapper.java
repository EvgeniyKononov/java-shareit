package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.Item;

public interface ItemMapper {
    Item toEntity(ItemDto dto, Long id);

    ItemDto toDto(Item item, Long userId);

    ItemDto toDto(Item item);

    Item createNewEntity(Long userId, ItemDto dto);
}
