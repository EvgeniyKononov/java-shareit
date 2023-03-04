package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto createBooking(Long id, BookingInputDto inputDto);

    BookingOutputDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingOutputDto findBooking(Long userId, Long bookingId);

    List<BookingOutputDto> findAllBookingsByBookerId(Long userId, String state);

    List<BookingOutputDto> findAllBookingsByOwnerId(Long userId, String state);
}
