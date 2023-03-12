package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findByBookerId() {
    }

    @Test
    void findAllByItem_Owner_Id() {
    }

    @Test
    void findFirstByItem_IdAndStatusAndStartAfterOrderByStart() {
    }

    @Test
    void findFirstByItem_IdAndStatusAndEndBeforeOrderByEnd() {
    }

    @Test
    void findFirstByItem_IdAndEndBeforeAndStatus() {
    }
}