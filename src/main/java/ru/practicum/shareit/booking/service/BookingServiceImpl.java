package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constant.Constant.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingMapper bookingMapper,
                              JpaItemRepository itemRepository,
                              JpaUserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingOutputDto createBooking(Long id, BookingDto bookingDto) {
        checkItem(bookingDto.getItemId());
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new IncorrectTimeException(INCORRECT_TIME_MSG);
        }
        if (bookingDto.getItemId().equals(id)) {
            throw new AuthOwnerException(BOOK_MISTAKE_MSG);
        }
        Booking booking = bookingRepository.save(bookingMapper.toNewEntity(id, bookingDto));
        return bookingMapper.toOutputDto(booking);
    }

    @Override
    public BookingOutputDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking updatedBooking = booking.get();
            if ((updatedBooking.getStatus().equals(Status.APPROVED)
                    || updatedBooking.getStatus().equals(Status.REJECTED))
                    && Objects.nonNull(approved)) {
                throw new UnsupportedStatusException(STATUS_MISTAKE_MSG);
            }
            if (isOwner(userId, updatedBooking)) {
                if (approved) {
                    updatedBooking.setStatus(Status.APPROVED);
                } else {
                    updatedBooking.setStatus(Status.REJECTED);
                }
                return bookingMapper.toOutputDto(bookingRepository.save(updatedBooking));
            } else {
                throw new AuthOwnerException(USER_NOT_OWNER_MSG);
            }
        } else {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
    }

    @Override
    public BookingOutputDto findBooking(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking searchedBooking = booking.get();
            if (isOwner(userId, searchedBooking) || isBooker(userId, searchedBooking)) {
                return bookingMapper.toOutputDto(searchedBooking);
            } else {
                throw new AuthOwnerException(USER_NOT_OWNER_OR_BOOKER_MSG);
            }
        } else {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
    }

    @Override
    public List<BookingOutputDto> findAllBookingsByBookerId(Long userId, String searchedState) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
        List<Booking> bookings = bookingRepository.findByBookerId(userId);
        List<BookingOutputDto> bookingsDto = new ArrayList<>();
        return getBookingDtos(searchedState, bookings, bookingsDto);
    }

    @Override
    public List<BookingOutputDto> findAllBookingsByOwnerId(Long userId, String searchedState) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
        List<Item> items = itemRepository.findAllByOwner(userId);
        List<Booking> bookings = new ArrayList<>();
        List<BookingOutputDto> bookingsDto = new ArrayList<>();
        for (Item item : items) {
            bookings.addAll(bookingRepository.findByItem(item.getId()));
        }
        return getBookingDtos(searchedState, bookings, bookingsDto);
    }

    private List<BookingOutputDto> getBookingDtos(String searchedState, List<Booking> bookings,
                                                  List<BookingOutputDto> bookingsDto) {
        for (Booking booking : bookings) {
            bookingsDto.add(bookingMapper.toOutputDto(booking));
        }
        if (searchedState.equals("ALL")) {
            return bookingsDto.stream()
                    .sorted((Comparator.comparing(BookingOutputDto::getStart)).reversed())
                    .collect(Collectors.toList());
        } else if (Objects.equals(checkAndReturnState(searchedState), State.WAITING)) {
            return bookingsDto.stream()
                    .filter(dto -> dto.getStatus().equals(Status.WAITING))
                    .sorted((Comparator.comparing(BookingOutputDto::getStart)).reversed())
                    .collect(Collectors.toList());
        } else if (Objects.equals(checkAndReturnState(searchedState), State.CURRENT)) {
            List<BookingOutputDto> currentBookingsDto = new ArrayList<>();
            for (BookingOutputDto bookingOutputDto : bookingsDto) {
                if (!bookingOutputDto.getState(bookingOutputDto).equals(State.FUTURE)
                        && !bookingOutputDto.getState(bookingOutputDto).equals(State.PAST))
                    currentBookingsDto.add(bookingOutputDto);
            }
            return currentBookingsDto;
        } else {
            return bookingsDto.stream()
                    .filter(dto -> dto.getState(dto).equals(checkAndReturnState(searchedState)))
                    .sorted((Comparator.comparing(BookingOutputDto::getStart)).reversed())
                    .collect(Collectors.toList());
        }
    }

    private Boolean isOwner(Long userId, Booking booking) {
        List<Item> items = itemRepository.findAllByOwner(userId);
        for (Item item : items) {
            if (item.getId().equals(booking.getItem())) {
                return true;
            }
        }
        return false;
    }

    private Boolean isBooker(Long userId, Booking booking) {
        return userId.equals(booking.getBookerId());
    }

    private void checkItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(NOT_FOUND_ITEM_ID));
        if (!item.getAvailable()) {
            throw new NullPointerException(ITEM_IS_BOOKED_ALREADY_MSG);
        }
    }

    private State checkAndReturnState(String state) {
        if (!isStateExist(state)) {
            throw new IllegalArgumentException("Unknown state: " + state);
        } else
            return State.valueOf(state);
    }

    private boolean isStateExist(String state) {
        boolean exist = true;
        try {
            State.valueOf(state);
        } catch (IllegalArgumentException e) {
            exist = false;
        }
        return exist;
    }
}
