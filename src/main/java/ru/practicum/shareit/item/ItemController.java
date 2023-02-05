package ru.practicum.shareit.item;

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

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@RequestHeader("X-Sharer-User-Id")Long userId,
                        @PathVariable Long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<ItemDto> find(@RequestHeader("X-Sharer-User-Id")Long userId) {
        return itemService.findItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestHeader("X-Sharer-User-Id")Long userId,
                              @RequestParam/*(defaultValue = "")*/ String text) {
        return itemService.findItemsByText(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id")Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PatchMapping(value = "/{itemId}")
    public ItemDto amend(@RequestHeader("X-Sharer-User-Id")Long userId,
                         @PathVariable Long itemId,
                         @Valid @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@RequestHeader("X-Sharer-User-Id")Long userId,
                       @PathVariable Long id) {
        itemService.deleteItem(userId, id);
    }
}
