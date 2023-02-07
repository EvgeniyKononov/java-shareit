package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item amend(Item item);

    Item find(Long id);

    void delete(Long id);

    List<Item> getAll();
}
