package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constant.Constant.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              JpaItemRepository itemRepository,
                              JpaUserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingOutputDto createBooking(Long bookerId, BookingInputDto inputDto) {
        Booking booking = BookingMapper.toEntity(inputDto);
        Item item = checkAndGetItem(booking.getItem().getId());
        User booker = checkAndGetUser(bookerId);
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new IncorrectTimeException(INCORRECT_TIME_MSG);
        }
        if (Objects.equals(item.getOwner().getId(), bookerId)) {
            throw new AuthOwnerException(BOOK_MISTAKE_MSG);
        }
        Booking newBooking = new Booking(null, booking.getStart(), booking.getEnd(), item, booker, booking.getStatus());
        return BookingMapper.toOutputDto(bookingRepository.save(newBooking));
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
                return BookingMapper.toOutputDto(bookingRepository.save(updatedBooking));
            } else {
                throw new AuthOwnerException(USER_NOT_OWNER_MSG);
            }
        } else {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
    }

    @Override
    public BookingOutputDto findBooking(Long userId, Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            Booking searchedBooking = booking.get();
            if (isOwner(userId, searchedBooking) || isBooker(userId, searchedBooking)) {
                return BookingMapper.toOutputDto(searchedBooking);
            } else {
                throw new AuthOwnerException(USER_NOT_OWNER_OR_BOOKER_MSG);
            }
        } else {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
    }

    @Override
    public List<BookingOutputDto> findAllBookingsByBookerId(Long userId, String searchedState, PageRequest pageRequest) {
        List<Booking> bookings = bookingRepository.findByBookerId(userId);
        if (bookings.isEmpty()) {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
        List<BookingOutputDto> bookingsDto = BookingMapper.toOutputDtoList(getBookings(searchedState, bookings));
        return bookingsDto.subList(pageRequest.getPageNumber(),
                Math.min(bookingsDto.size(), pageRequest.getPageNumber() + pageRequest.getPageSize()));
    }

    @Override
    public List<BookingOutputDto> findAllBookingsByOwnerId(Long userId, String searchedState, PageRequest pageRequest) {
        List<Booking> bookings = new ArrayList<>(bookingRepository.findAllByItem_Owner_Id(userId));
        if (bookings.isEmpty()) {
            throw new NotFoundBookingException(NOT_FOUND_BOOKING_ID);
        }
        List<BookingOutputDto> bookingsDto = BookingMapper.toOutputDtoList(getBookings(searchedState, bookings));
        return bookingsDto.subList(pageRequest.getPageNumber(),
                Math.min(bookingsDto.size(), pageRequest.getPageNumber() + pageRequest.getPageSize()));
    }

    private List<Booking> getBookings(String searchedState, List<Booking> bookings) {
        if (searchedState.equals(ALL)) {
            return bookings.stream()
                    .sorted((Comparator.comparing(Booking::getStart)).reversed())
                    .collect(Collectors.toList());
        } else if (Objects.equals(checkAndReturnState(searchedState), State.WAITING)) {
            return bookings.stream()
                    .filter(booking -> booking.getStatus().equals(Status.WAITING))
                    .sorted((Comparator.comparing(Booking::getStart)).reversed())
                    .collect(Collectors.toList());
        } else if (Objects.equals(checkAndReturnState(searchedState), State.CURRENT)) {
            List<Booking> currentBookings = new ArrayList<>();
            for (Booking booking : bookings) {
                if (!Objects.equals(Booking.getState(booking), State.FUTURE)
                        && !Objects.equals(Booking.getState(booking), State.PAST))
                    currentBookings.add(booking);
            }
            return currentBookings;
        } else {
            return bookings.stream()
                    .filter(booking -> Objects.equals(Booking.getState(booking), checkAndReturnState(searchedState)))
                    .sorted((Comparator.comparing(Booking::getStart)).reversed())
                    .collect(Collectors.toList());
        }
    }

    private Boolean isOwner(Long userId, Booking booking) {
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        for (Item item : items) {
            if (item.getOwner().equals(booking.getItem().getOwner())) {
                return true;
            }
        }
        return false;
    }

    private Boolean isBooker(Long userId, Booking booking) {
        return userId.equals(booking.getBooker().getId());
    }

    private Item checkAndGetItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(NOT_FOUND_ITEM_ID));
        if (!item.getAvailable()) {
            throw new NullPointerException(ITEM_IS_BOOKED_ALREADY_MSG);
        }
        return item;
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundItemException(NOT_FOUND_USER_ID));
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
