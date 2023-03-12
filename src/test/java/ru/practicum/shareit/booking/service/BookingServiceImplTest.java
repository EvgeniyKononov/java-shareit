package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private JpaItemRepository itemRepository;
    @Mock
    private JpaUserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    Long bookerId;
    Long itemId;
    Long userId;
    Long bookingId;
    BookingInputDto inputDto;
    BookingOutputDto outputDto;
    LocalDateTime current;
    private Item item;
    private User user;
    private Long requestId;
    private ItemRequest itemRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookerId = 1L;
        itemId = 2L;
        userId = 3L;
        requestId = 4L;
        bookingId = 5L;
        current = LocalDateTime.now();
        inputDto = BookingInputDto.builder()
                .itemId(itemId)
                .start(current.plusDays(1))
                .end(current.plusDays(2))
                .build();
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
        item = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(user)
                .build();
        booking = Booking.builder()
                .id(bookingId)
                .start(current.plusDays(1))
                .end(current.plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
        outputDto = BookingOutputDto.builder()
                .id(bookingId)
                .start(current.plusDays(1))
                .end(current.plusDays(2))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void createBooking_whenItemNotFound_thenNotFoundItemExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class,
                () -> bookingService.createBooking(bookerId, inputDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenItemNotAvailable_thenNullPointerExceptionThrown() {
        item.setAvailable(false);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NullPointerException.class,
                () -> bookingService.createBooking(bookerId, inputDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenUserNotFound_thenNotFoundItemExceptionThrown() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class,
                () -> bookingService.createBooking(bookerId, inputDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenIncorrectTiming_thenIncorrectTimeExceptionThrown() {
        inputDto.setStart(current.plusDays(2));
        inputDto.setEnd(current.plusDays(1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));

        assertThrows(IncorrectTimeException.class,
                () -> bookingService.createBooking(bookerId, inputDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBookerIsOwner_thenAuthOwnerExceptionThrown() {
        user.setId(1L);
        bookerId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));

        assertThrows(AuthOwnerException.class,
                () -> bookingService.createBooking(bookerId, inputDto));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenNoAnyException_thenSaveBookingAndReturnBookingOutputDto() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingOutputDto actualDto = bookingService.createBooking(bookerId, inputDto);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void updateBooking_whenBookingNotFound_thenNotFoundBookingExceptionThrown() {
        Boolean approved = true;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookingException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingRejected_thenUnsupportedStatusExceptionThrown() {
        Boolean approved = true;
        booking.setStatus(Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(UnsupportedStatusException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenUserIsNotItemOwner_thenAuthOwnerExceptionThrown() {
        Boolean approved = true;
        User newUser = new User();
        Item newItem = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(newUser)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(newItem));

        assertThrows(AuthOwnerException.class,
                () -> bookingService.updateBooking(userId, bookingId, approved));

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateBooking_whenStatusRejected_thenBookingRejectedAndReturnDto() {
        Boolean approved = false;
        outputDto.setStatus(Status.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingOutputDto actualDto = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void updateBooking_whenStatusApproved_thenBookingApprovedAndReturnDto() {
        Boolean approved = true;
        outputDto.setStatus(Status.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingOutputDto actualDto = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void findBooking_whenBookingNotFound_thenNotFoundBookingExceptionThrown() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundBookingException.class,
                () -> bookingService.findBooking(userId, bookingId));

    }

    @Test
    void findBooking_whenUserIsNotItemOwnerOrBooker_thenAuthOwnerExceptionThrown() {
        User newUser = new User();
        userId = 100L;
        Item newItem = Item.builder()
                .id(itemId)
                .name("name")
                .description("description")
                .available(true)
                .request(itemRequest)
                .owner(newUser)
                .build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(newItem));

        assertThrows(AuthOwnerException.class,
                () -> bookingService.findBooking(userId, bookingId));
    }

    @Test
    void findBooking_whenBookingFound_thenReturnDto() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findAllByOwner_Id(userId)).thenReturn(List.of(item));

        BookingOutputDto actualDto = bookingService.findBooking(userId, bookingId);

        assertEquals(outputDto, actualDto);
    }

    @Test
    void findAllBookingsByBookerId_whenBookingsNotFound_thenNotFoundBookingExceptionThrown() {
        String searchedState = "ALL";
        when(bookingRepository.findByBookerId(userId)).thenReturn(new ArrayList<>());

        assertThrows(NotFoundBookingException.class,
                () -> bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10)));
    }

    @Test
    void findAllBookingsByBookerId_whenStateAll_thenReturnAllBookings() {
        String searchedState = "ALL";
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateWaiting_thenReturnWaitingBookings() {
        String searchedState = "WAITING";
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateCurrent_thenReturnCurrentBookings() {
        String searchedState = "CURRENT";
        booking.setStart(current.minusDays(1));
        booking.setEnd(current.plusDays(1));
        outputDto.setStart(current.minusDays(1));
        outputDto.setEnd(current.plusDays(1));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateFuture_thenReturnFutureBookings() {
        String searchedState = "FUTURE";
        booking.setStart(current.plusDays(1));
        booking.setEnd(current.plusDays(2));
        outputDto.setStart(current.plusDays(1));
        outputDto.setEnd(current.plusDays(2));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStatePast_thenReturnPastBookings() {
        String searchedState = "PAST";
        booking.setStart(current.minusDays(2));
        booking.setEnd(current.minusDays(1));
        outputDto.setStart(current.minusDays(2));
        outputDto.setEnd(current.minusDays(1));
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateRejected_thenReturnRejectedBookings() {
        String searchedState = "REJECTED";
        booking.setStatus(Status.CANCELLED);
        outputDto.setStatus(Status.CANCELLED);
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }

    @Test
    void findAllBookingsByBookerId_whenStateUnknown_thenIllegalArgumentExceptionThrown() {
        String searchedState = "UNKNOWN";
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        when(bookingRepository.findByBookerId(userId)).thenReturn(bookings);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findAllBookingsByBookerId(userId, searchedState, PageRequest.of(0, 10)));
    }
    @Test
    void findAllBookingsByOwnerId_whenBookingsNotFound_thenNotFoundBookingExceptionThrown() {
        String searchedState = "ALL";
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(new ArrayList<>());

        assertThrows(NotFoundBookingException.class,
                () -> bookingService.findAllBookingsByOwnerId(userId, searchedState, PageRequest.of(0, 10)));
    }

    @Test
    void findAllBookingsByOwnerId_whenStateAll_thenReturnAllBookings() {
        String searchedState = "ALL";
        List<Booking> bookings = new ArrayList<>(List.of(booking));
        List<BookingOutputDto> expectedBookingsDto = new ArrayList<>(List.of(outputDto));
        when(bookingRepository.findAllByItem_Owner_Id(userId)).thenReturn(bookings);

        List<BookingOutputDto> actualBookingsDto
                = bookingService.findAllBookingsByOwnerId(userId, searchedState, PageRequest.of(0, 10));

        assertEquals(expectedBookingsDto, actualBookingsDto);
    }
}