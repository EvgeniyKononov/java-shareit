package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundRequestException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    private Long userId;
    private ItemRequestDto requestDto1;
    private ItemRequestDto requestDto2;
    private User user;
    private ItemRequest itemRequest;
    private Long requestId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        requestId = 1L;
        user = new User();
        requestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        ;
        requestDto2 = new ItemRequestDto();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(user)
                .creationDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void createRequest_whenUserNotFound_thenNotFoundUserExceptionThrown() {
        when(userRepository.findById(userId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class,
                () -> requestService.createRequest(userId, requestDto1));
    }

    @Test
    void createRequest_whenUserFound_thenReturnRequestDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        requestDto2 = requestService.createRequest(userId, requestDto1);

        assertEquals(requestDto1, requestDto2);
    }

    @Test
    void findAllByUserId_whenUserNotFound_thenNotFoundUserExceptionThrown() {
        when(userRepository.findById(userId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class,
                () -> requestService.findAllByUserId(userId));
    }

    @Test
    void findAllByUserId_whenUserFound_thenReturnRequestDto() {
        List<ItemRequest> requests = new ArrayList<>(List.of(itemRequest));
        List<ItemRequestDto> itemRequestDtos1 = new ArrayList<>(List.of(requestDto1));
        when(requestRepository.findAllByRequestor_IdOrderByCreationDateTimeDesc(userId))
                .thenReturn(requests);

        List<ItemRequestDto> itemRequestDtos2 = requestService.findAllByUserId(userId);

        assertEquals(itemRequestDtos2, itemRequestDtos1);
    }

    @Test
    void findAll_whenUserFound_thenReturnRequestDto() {
        Page<ItemRequest> requests = new PageImpl<>(List.of(itemRequest));
        List<ItemRequestDto> itemRequestDtos1 = new ArrayList<>(List.of(requestDto1));
        when(requestRepository.findAllByRequestorIdWithoutRequester(userId, PageRequest.of(0, 10)))
                .thenReturn(requests);

        List<ItemRequestDto> itemRequestDtos2 = requestService.findAll(userId, PageRequest.of(0, 10));

        assertEquals(itemRequestDtos1, itemRequestDtos2);
    }

    @Test
    void findAll_whenUserNotFound_thenNotFoundUserExceptionThrown() {
        when(requestRepository.findAllByRequestorIdWithoutRequester(userId, PageRequest.of(0, 10)))
                .thenReturn(Page.empty());
        when(userRepository.findById(userId)).thenThrow(NotFoundUserException.class);

        assertThrows(NotFoundUserException.class,
                () -> requestService.findAll(userId, PageRequest.of(0, 10)));
    }

    @Test
    void findById_whenRequestNotFound_thenNotFoundRequestExceptionThrown() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenThrow(NotFoundRequestException.class);

        assertThrows(NotFoundRequestException.class,
                () -> requestService.findById(userId, requestId));
    }

    @Test
    void findAll_whenRequestFound_thenReturnRequestDto() {
        Item item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .build();
        List<Item> items = new ArrayList<>(List.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(1L)).thenReturn(items);

        requestDto2 = requestService.findById(userId, requestId);

        assertEquals(requestDto1, requestDto2);
    }
}