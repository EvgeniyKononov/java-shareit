package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
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
    public List<ItemDto> find(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestHeader(USER_ID_HEADER) Long userId,
                              @RequestParam String text) {
        return itemService.findItemsByText(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID_HEADER) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
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
                             @Valid @RequestBody CommentDto commentDto) {
        return itemService.createComment(userId, itemId, commentDto);
    }
}
