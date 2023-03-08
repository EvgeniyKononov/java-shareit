package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;


public class BookingMapper {

    public static Booking toEntity(BookingInputDto dto) {
        return new Booking(
                null,
                dto.getStart(),
                dto.getEnd(),
                Item.builder().id(dto.getItemId()).build(),
                null,
                Status.WAITING
        );
    }

    public static BookingOutputDto toOutputDto(Booking booking) {
        return BookingOutputDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingOutputDto> toOutputDtoList(List<Booking> bookings) {
        List<BookingOutputDto> dtoBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            dtoBookings.add(toOutputDto(booking));
        }
        return dtoBookings;
    }
}
