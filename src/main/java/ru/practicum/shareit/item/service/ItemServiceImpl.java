package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.exception.AuthOwnerException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.constant.Constant.*;

@Service
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;

    private final JpaUserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(JpaItemRepository itemRepository, JpaUserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return ItemMapper.toDto(itemRepository.save(ItemMapper.createNewEntity(user.get(), itemDto)));
        } else throw new NotFoundUserException(NOT_FOUND_USER_ID);
    }

    @Override
    public ItemDto findItem(Long userId, Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return ItemMapper.toDto(item.get(), userId, getLastBooking(itemId),
                    getNextBooking(itemId, getLastBooking(itemId)),
                    getCommentsDto(itemId));
        } else throw new NotFoundItemException(NOT_FOUND_USER_ID);
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwner_IdOrderById(userId);
        for (Item item : items) {
            Long itemId = item.getId();
            itemsDto.add(ItemMapper.toDto(item, userId, getLastBooking(itemId),
                    getNextBooking(itemId, getLastBooking(itemId)), getCommentsDto(itemId)));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemRepository.findAll());
        for (Item item : items) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() &&
                    !text.equals("")) {
                itemsDto.add(ItemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            if (Objects.equals(item.get().getOwner().getId(), userId)) {
                return ItemMapper.toDto(itemRepository.save(ItemMapper.toEntity(itemDto, itemId, item.get())));
            } else {
                throw new AuthOwnerException(USER_NOT_OWNER_MSG);
            }
        } else {
            throw new NotFoundItemException(NOT_FOUND_ITEM_ID);
        }
    }

    @Override
    public void deleteItem(Long userId, Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Booking booking = bookingRepository
                .findFirstByItem_IdAndEndBeforeAndStatus(itemId, LocalDateTime.now(), Status.APPROVED)
                .orElseThrow(() -> new UnsupportedStatusException(ITEM_WAS_NOT_BOOKED_BY_USER_MSG));
        if (booking.getBooker().getId() == userId
                && Objects.equals(Booking.getState(booking), State.PAST)
                && !Objects.equals(booking.getStatus(), (Status.CANCELLED))
                && !Objects.equals(booking.getStatus(), (Status.REJECTED))) {
            return CommentMapper.toDto(commentRepository.save(CommentMapper.toNewEntity(commentDto,
                    itemRepository.findById(itemId).get(), userRepository.findById(userId).get())));
        } else
            throw new UnsupportedStatusException(ITEM_WAS_NOT_BOOKED_BY_USER_MSG);
    }

    private Booking getNextBooking(Long itemId, Booking lastBooking) {
        LocalDateTime lastBookingEnd;
        if (Objects.isNull(lastBooking)) {
            lastBookingEnd = LocalDateTime.now();
        } else {
            lastBookingEnd = lastBooking.getEnd();
        }
        return bookingRepository.findFirstByItem_IdAndStatusAndStartAfterOrderByStart(itemId,
                Status.APPROVED, lastBookingEnd).orElse(null);
    }

    private Booking getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItem_IdAndStatusAndEndBeforeOrderByEnd(itemId,
                Status.APPROVED, LocalDateTime.now()).orElse(null);
    }

    private List<CommentDto> getCommentsDto(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toDto(comment));
        }
        return commentsDto;
    }
}
