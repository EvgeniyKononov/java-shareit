package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long id, Booking booking);

    Booking updateBooking(Long userId, Long bookingId, Boolean approved);

    Booking findBooking(Long userId, Long bookingId);

    List<Booking> findAllBookingsByBookerId(Long userId, String state);

    List<Booking> findAllBookingsByOwnerId(Long userId, String state);
}
