package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingOutputDto create(@RequestHeader(userIdHeader) Long userId,
                                   @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public BookingOutputDto amend(@RequestHeader(userIdHeader) Long userId,
                                  @PathVariable Long bookingId,
                                  @RequestParam boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingOutputDto find(@RequestHeader(userIdHeader) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutputDto> findAllByBooker(@RequestHeader(userIdHeader) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByBookerId(userId, state);
    }

    @GetMapping(value = "/owner")
    public List<BookingOutputDto> findAllByOwner(@RequestHeader(userIdHeader) Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllBookingsByOwnerId(userId, state);
    }
}
