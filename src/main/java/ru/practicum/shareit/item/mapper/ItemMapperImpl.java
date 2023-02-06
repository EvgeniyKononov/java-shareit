package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Objects;


@Component
public class ItemMapperImpl implements ItemMapper {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemMapperImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createNewEntity(Long userId, ItemDto dto) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                userId,
                null
        );
    }

    @Override
    public Item toEntity(ItemDto dto, Long id) {
        Item item = itemRepository.find(id);
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();
        if (Objects.nonNull(dto.getName())) {
            name = dto.getName();
        }
        if (Objects.nonNull(dto.getDescription())) {
            description = dto.getDescription();
        }
        if (Objects.nonNull(dto.getAvailable())) {
            available = dto.getAvailable();
        }
        return new Item(
                id,
                name,
                description,
                available,
                item.getOwner(),
                item.getItemRequest()
        );
    }

    @Override
    public ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }
}
