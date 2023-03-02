package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
public class ItemMapperImpl implements ItemMapper {
    private final JpaItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemMapperImpl(JpaItemRepository itemRepository,
                          BookingRepository bookingRepository,
                          CommentRepository commentRepository,
                          CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
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
        Optional<Item> itemOptional = itemRepository.findById(id);
        Item item = new Item();
        if (itemOptional.isPresent()) {
            item = itemOptional.get();
        }
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

    @Override
    public ItemDto toDto(Item item, Long userId) {
        Booking lastBooking;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking = item.getOwner().equals(userId) ? getLastBooking(item.getId()) : null,
                item.getOwner().equals(userId) ? getNextBooking(item.getId(), lastBooking) : null,
                getCommentsDto(item.getId())
        );
    }

    @Override
    public ItemDto toDto(Item item) {
        Booking lastBooking;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking = getLastBooking(item.getId()),
                getNextBooking(item.getId(), lastBooking),
                getCommentsDto(item.getId())
        );
    }

    private Booking getNextBooking(Long itemId, Booking lastBooking) {
        LocalDateTime lastBookingEnd;
        if (Objects.isNull(lastBooking)) {
            lastBookingEnd = LocalDateTime.now();
        } else {
            lastBookingEnd = lastBooking.getEnd();
        }
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(itemId,
                Status.APPROVED, lastBookingEnd).orElse(null);
    }

    private Booking getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemAndStatusAndEndBeforeOrderByEnd(itemId,
                Status.APPROVED, LocalDateTime.now()).orElse(null);
    }

    private List<CommentDto> getCommentsDto(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(commentMapper.toDto(comment));
        }
        return commentsDto;
    }
}
