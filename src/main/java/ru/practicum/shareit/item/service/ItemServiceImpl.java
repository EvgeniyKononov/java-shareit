package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import ru.practicum.shareit.booking.model.BookingOutputDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.exception.AuthOwnerException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.shareit.constant.Constant.*;

@Service
public class ItemServiceImpl implements ItemService {

    private final JpaItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final JpaUserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemServiceImpl(JpaItemRepository itemRepository, ItemMapper itemMapper, JpaUserRepository userRepository,
                           BookingRepository bookingRepository, BookingMapper bookingMapper,
                           CommentRepository commentRepository, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = itemMapper.createNewEntity(userId, itemDto);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return itemMapper.toDto(itemRepository.save(item));
        } else throw new NotFoundUserException(NOT_FOUND_USER_ID);
    }

    @Override
    public ItemDto findItem(Long userId, Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            return itemMapper.toDto(item.get(), userId);
        } else throw new NotFoundItemException(NOT_FOUND_USER_ID);
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = itemRepository.findAllByOwnerOrderById(userId);
        for (Item item : items) {
            itemsDto.add(itemMapper.toDto(item));
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
                itemsDto.add(itemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            if (Objects.equals(item.get().getOwner(), userId)) {
                return itemMapper.toDto(itemRepository.save(itemMapper.toEntity(itemDto, itemId)));
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
        BookingOutputDto bookingDto = bookingMapper.toOutputDto(bookingRepository
                .findFirstByItemAndEndBeforeAndStatus(itemId, LocalDateTime.now(), Status.APPROVED)
                .orElseThrow(() -> new UnsupportedStatusException(ITEM_WAS_NOT_BOOKED_BY_USER_MSG)));
        if (bookingDto.getBooker().getId() == userId
                && bookingDto.getState(bookingDto).equals(State.PAST)
                && !Objects.equals(bookingDto.getStatus(), (Status.CANCELLED))
                && !Objects.equals(bookingDto.getStatus(), (Status.REJECTED))) {
            return commentMapper.toDto(commentRepository.save(commentMapper.toNewEntity(commentDto, itemId, userId)));
        } else
            throw new UnsupportedStatusException(ITEM_WAS_NOT_BOOKED_BY_USER_MSG);
    }
}
