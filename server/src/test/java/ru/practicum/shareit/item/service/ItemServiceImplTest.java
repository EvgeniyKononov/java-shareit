package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AuthOwnerException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    private Long userId;
    private Long itemId;
    private Long requestId;
    private ItemDto itemDto;
    private User user;
    private ItemRequest itemRequest;
    private Item item;
    private Booking booking;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = 1L;
        itemId = 2L;
        requestId = 3L;
        user = User.builder()
                .id(userId)
                .name("userName")
                .email("em@em.em")
                .build();
        itemRequest = ItemRequest.builder()
                .id(requestId)
                .description("request description")
                .requestor(user)
                .creationDateTime(LocalDateTime.now())
                .build();
        itemDto = ItemDto.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .requestId(requestId)
                .build();
        item = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(user)
                .build();
        booking = new Booking();
        commentDto = new CommentDto();
        comment = new Comment();

    }

    @Test
    void createItem_whenUserAndRequestFound_thenReturnDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto newItemDto = itemService.createItem(userId, itemDto);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void createItem_whenUserAndRequestNotFound_thenReturnDto() {
        item.setRequest(null);
        itemDto.setRequestId(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto newItemDto = itemService.createItem(userId, itemDto);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void createItem_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> itemService.createItem(userId, itemDto));
    }

    @Test
    void findItem_whenUserFound_thenReturnDto() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto newItemDto = itemService.findItem(userId, itemId);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void findItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class, () -> itemService.findItem(userId, itemId));
    }

    @Test
    void findItemsByOwner_whenUserFound_thenReturnDtoList() {
        List<ItemDto> expectedItemsDto = new ArrayList<>(List.of(itemDto));
        List<Item> items = new ArrayList<>(List.of(item));
        when(itemRepository.findAllByOwner_IdOrderById(userId, PageRequest.of(0, 10)))
                .thenReturn(items);

        List<ItemDto> newItemsDto = itemService.findItemsByOwner(userId, PageRequest.of(0, 10));

        assertEquals(expectedItemsDto, newItemsDto);
    }

    @Test
    void findItemsByText_whenUserFound_thenReturnDtoList() {
        List<ItemDto> expectedItemsDto = new ArrayList<>(List.of(itemDto));
        List<Item> items = new ArrayList<>(List.of(item));
        when(itemRepository.findAll()).thenReturn(items);

        List<ItemDto> newItemsDto = itemService.findItemsByText("descr", PageRequest.of(0, 10));

        assertEquals(expectedItemsDto, newItemsDto);
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class, () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItem_whenUserNotOwner_thenAuthOwnerExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        userId = 100L;

        assertThrows(AuthOwnerException.class, () -> itemService.updateItem(userId, itemId, itemDto));
    }

    @Test
    void updateItem_whenItemFound_thenReturnDto() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto newItemDto = itemService.updateItem(userId, itemId, itemDto);

        assertEquals(newItemDto, itemDto);
    }

    @Test
    void deleteItem_whenItemFound_thenDelete() {

        itemService.deleteItem(userId, itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void createComment_whenUserNotBooker_thenUnsupportedStatusExceptionThrown() {
        when(bookingRepository
                .findFirstByItem_IdAndEndBeforeAndStatus(any(Long.class), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(Optional.empty());

        assertThrows(UnsupportedStatusException.class, () -> itemService.createComment(userId, itemId, commentDto));
    }

    @Test
    void createComment_whenStatusRejected_thenUnsupportedStatusExceptionThrown() {
        booking.setBooker(user);
        booking.setStatus(Status.REJECTED);
        when(bookingRepository
                .findFirstByItem_IdAndEndBeforeAndStatus(any(Long.class), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(Optional.of(booking));

        assertThrows(UnsupportedStatusException.class, () -> itemService.createComment(userId, itemId, commentDto));
    }

    @Test
    void createComment_whenStatePast_thenReturnCommentDto() {
        LocalDateTime current = LocalDateTime.now();
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(current.minusDays(2));
        booking.setEnd(current.minusDays(1));
        comment.setItem(item);
        comment.setUser(user);
        comment.setText("comment");
        commentDto.setItemId(itemId);
        commentDto.setAuthorName("userName");
        commentDto.setText("comment");
        when(bookingRepository
                .findFirstByItem_IdAndEndBeforeAndStatus(any(Long.class), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(Optional.of(booking));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto newCommentDto = itemService.createComment(userId, itemId, commentDto);

        assertEquals(newCommentDto, commentDto);
    }
}