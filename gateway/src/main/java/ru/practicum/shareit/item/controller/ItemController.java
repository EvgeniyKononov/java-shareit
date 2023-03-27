package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.constant.Constant.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> find(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PathVariable Long itemId) {
        log.info("Get item with id = {}, userId = {}", itemId, userId);
        return itemClient.findItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> find(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        log.info("Get item by ownerId = {}", userId);
        return itemClient.findItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> find(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @RequestParam String text,
                                       @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                       @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        log.info("Get item by text = {}", text);
        return itemClient.findItemsByText(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ResponseEntity<Object> amend(@RequestHeader(USER_ID_HEADER) Long userId,
                                        @PathVariable Long itemId,
                                        @RequestBody ItemRequestDto itemDto) {
        log.info("Updating item {} with id = {}, userId={}", itemDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long id) {
        log.info("Deleting item with id = {}, userId={}", id, userId);
        return itemClient.deleteItem(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long itemId,
                                         @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Creating comment {} for item with id {}, userId={}", commentDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
