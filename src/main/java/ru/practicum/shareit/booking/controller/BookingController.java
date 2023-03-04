package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.constant.Constant.USER_ID_HEADER;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingOutputDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @Valid @RequestBody BookingInputDto bookingDto) {
        return BookingMapper.toOutputDto(bookingService.createBooking(userId, BookingMapper.toEntity(bookingDto)));
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutputDto amend(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam boolean approved) {
        return BookingMapper.toOutputDto(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping(value = "/{bookingId}")
    public BookingOutputDto find(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        return BookingMapper.toOutputDto(bookingService.findBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingOutputDto> findAllByBooker(@RequestHeader(USER_ID_HEADER) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toOutputDtoList(bookingService.findAllBookingsByBookerId(userId, state));
    }

    @GetMapping(value = "/owner")
    public List<BookingOutputDto> findAllByOwner(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toOutputDtoList(bookingService.findAllBookingsByOwnerId(userId, state));
    }
}
