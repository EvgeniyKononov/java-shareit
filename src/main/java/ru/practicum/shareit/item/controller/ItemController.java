package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String userId = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader(userId) Long userId,
                        @PathVariable Long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<ItemDto> find(@RequestHeader(userId) Long userId) {
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestHeader(userId) Long userId,
                              @RequestParam String text) {
        return itemService.findItemsByText(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(userId) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto amend(@RequestHeader(userId) Long userId,
                         @PathVariable Long itemId,
                         @Valid @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader(userId) Long userId,
                       @PathVariable Long id) {
        itemService.deleteItem(userId, id);
    }
}
