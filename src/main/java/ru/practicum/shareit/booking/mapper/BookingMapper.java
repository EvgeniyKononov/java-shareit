package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingOutputDto;

public interface BookingMapper {
    Booking toNewEntity(Long id, BookingDto dto);

    Booking toEntity(BookingDto dto);

    BookingDto toDto(Booking booking);

    BookingOutputDto toOutputDto(Booking booking);
}
