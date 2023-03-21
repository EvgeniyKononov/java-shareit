package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constant.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody RequestDto requestDto) {
        log.info("Creating request {}, userId={}", requestDto, userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get requests by user {}", userId);
        return requestClient.findAllByUserId(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                          @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        log.info("Get all requests");
        return requestClient.findAll(userId, from, size);
    }

    @GetMapping(value = "/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long requestId) {
        log.info("Get requests with id = {}", requestId);
        return requestClient.findById(userId, requestId);
    }
}
