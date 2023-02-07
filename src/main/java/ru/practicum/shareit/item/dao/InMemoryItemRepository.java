package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item save(Item item) {
        item.setId(id);
        items.put(id, item);
        id++;
        return item;
    }

    @Override
    public Item amend(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item find(Long id) {
        return items.get(id);
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }
}
