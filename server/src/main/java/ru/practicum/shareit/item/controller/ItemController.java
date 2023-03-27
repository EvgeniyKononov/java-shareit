package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.constant.Constant.USER_ID_HEADER;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader(USER_ID_HEADER) Long userId,
                        @PathVariable Long itemId) {
        return itemService.findItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> find(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestParam(required = false, defaultValue = "0") int from,
                              @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.findItemsByOwner(userId, PageRequest.of(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestParam String text,
                              @RequestParam(required = false, defaultValue = "0") int from,
                              @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.findItemsByText(text, PageRequest.of(from, size));
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto amend(@RequestHeader(USER_ID_HEADER) Long userId,
                         @PathVariable Long itemId,
                         @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader(USER_ID_HEADER) Long userId,
                       @PathVariable Long id) {
        itemService.deleteItem(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                             @PathVariable Long itemId,
                             @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
