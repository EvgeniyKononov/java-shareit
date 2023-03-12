package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwner_Id(Long owner);

    List<Item> findAllByOwner_IdOrderById(Long owner, PageRequest pageRequest);

    List<Item> findAllByRequest_Id(Long id);
}
