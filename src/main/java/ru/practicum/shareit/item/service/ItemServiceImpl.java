package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.exception.AuthOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        if (Objects.isNull(userRepository.find(userId))) {
            throw new NotFoundUserException("Нет пользователя с таким id");
        }
        ;
        return itemMapper.toDto(itemRepository.save(itemMapper.createNewEntity(userId, itemDto)));
    }

    @Override
    public ItemDto findItem(Long id) {
        Item item = itemRepository.find(id);
        return itemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findItemsByOwner(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemRepository.getAll());
        for (Item item : items) {
            if (Objects.equals(item.getOwner(), userId)) {
                itemsDto.add(itemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> findItemsByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        List<Item> items = new ArrayList<>(itemRepository.getAll());
        for (Item item : items) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                    item.getAvailable() &&
                    text != "") {
                itemsDto.add(itemMapper.toDto(item));
            }
        }
        return itemsDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.find(itemId);
        if (item.getOwner() == userId) {
            return itemMapper.toDto(itemRepository.amend(itemMapper.toEntity(itemDto, itemId)));
        } else {
            throw new AuthOwnerException("Пользователь не является владельцем вещи");
        }
    }

    @Override
    public void deleteItem(Long userId, Long id) {
        itemRepository.delete(id);
    }
}
