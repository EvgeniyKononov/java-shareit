package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constant.*;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) long userId,
                                         @RequestBody @Valid BookItemRequestDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping(value = "/{bookingId}")
    public ResponseEntity<Object> amend(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam String approved) {
        log.info("Updating booking with id={}, userId={}", bookingId, userId);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> find(@RequestHeader(USER_ID_HEADER) long userId,
                                       @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByBooker(@RequestHeader(USER_ID_HEADER) long userId,
                                                  @RequestParam(name = STATE, defaultValue = ALL) String stateParam,
                                                  @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings by booker with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(USER_ID_HEADER) long userId,
                                                 @RequestParam(name = STATE, defaultValue = ALL) String stateParam,
                                                 @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings by owner with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findAllByOwner(userId, state, from, size);
    }
}
