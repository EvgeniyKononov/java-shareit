package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaItemRepositoryIT {

    @Autowired
    private JpaItemRepository itemRepository;

    @Test
    void findAllByOwner_Id() {
    }

    @Test
    void findAllByOwner_IdOrderById() {
    }

    @Test
    void findAllByRequest_Id() {
    }
}