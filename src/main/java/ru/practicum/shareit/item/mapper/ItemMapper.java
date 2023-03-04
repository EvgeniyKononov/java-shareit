package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;

public class ItemMapper {

    public static Item toEntity(ItemDto dto, Long id, Item item) {
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
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static ItemDto toDto(Item item, Long userId, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        Booking last = Objects.equals(item.getOwner().getId(), (userId)) ? lastBooking : null;
        Booking next = Objects.equals(item.getOwner().getId(), (userId)) ? nextBooking : null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                Objects.nonNull(last) ? new ItemDto.BookingsInItem(last) : null,
                Objects.nonNull(next) ? new ItemDto.BookingsInItem(next) : null,
                comments
        );
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null
        );
    }

    public static Item createNewEntity(User user, ItemDto dto) {
        return new Item(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getAvailable(),
                user,
                null
        );
    }
}
