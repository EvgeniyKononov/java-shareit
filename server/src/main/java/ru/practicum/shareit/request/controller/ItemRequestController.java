package ru.practicum.shareit.request.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.USER_ID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestService.findAllByUserId(userId);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> findAll(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @RequestParam(required = false, defaultValue = "0") int from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {
        return requestService.findAll(userId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{requestId}")
    public ItemRequestDto findById(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @PathVariable Long requestId) {
        return requestService.findById(userId, requestId);
    }
}
