package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIT {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private JpaItemRepository itemRepository;
    @Autowired
    private JpaUserRepository userRepository;

    private User user1;
    private User user2;
    private Item item;
    LocalDateTime current;

    @BeforeEach
    public void addRequest() {
        current = LocalDateTime.now();
        user1 = userRepository.save(User.builder()
                .name("Name 1")
                .email("name@name.ru")
                .build());
        user2 = userRepository.save(User.builder()
                .name("Name 2")
                .email("name2@name.ru")
                .build());
        item = itemRepository.save(Item.builder()
                .name("вещь")
                .description("супер вещь")
                .available(true)
                .owner(user1)
                .build());
        itemRepository.save(Item.builder()
                .name("печь")
                .description("супер печь")
                .available(true)
                .owner(user2)
                .build());
        bookingRepository.save(Booking.builder()
                .start(current)
                .end(current.plusDays(1))
                .item(item)
                .booker(user2)
                .status(Status.WAITING)
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findByBookerId_whenInvoke_listBookingsReturn() {
        List<Booking> bookings = bookingRepository.findByBookerId(user2.getId());

        assertEquals(1, bookings.size());
        assertEquals("супер вещь", bookings.get(0).getItem().getDescription());
    }

    @Test
    void findAllByItem_Owner_Id_whenInvoke_listBookingsReturn() {
        List<Booking> bookings = bookingRepository.findAllByItem_Owner_Id(user1.getId());

        assertEquals(1, bookings.size());
        assertEquals("супер вещь", bookings.get(0).getItem().getDescription());
    }

    @Test
    void findFirstByItem_IdAndStatusAndStartAfterOrderByStart_whenInvoke_bookingReturn() {
        Optional<Booking> booking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartAfterOrderByStart(item.getId(), Status.WAITING, current.minusDays(1));

        assertTrue(booking.isPresent());
        assertEquals("супер вещь", booking.get().getItem().getDescription());
    }

    @Test
    void findFirstByItem_IdAndStatusAndEndBeforeOrderByEnd_whenInvoke_bookingReturn() {
        Optional<Booking> booking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(item.getId(), Status.WAITING, current.plusDays(2));

        assertTrue(booking.isPresent());
        assertEquals("супер вещь", booking.get().getItem().getDescription());
    }

    @Test
    void findFirstByItem_IdAndEndBeforeAndStatus_whenInvoke_bookingReturn() {
        Optional<Booking> booking = bookingRepository
                .findFirstByItem_IdAndEndBeforeAndStatus(item.getId(), current.plusDays(2), Status.WAITING);

        assertTrue(booking.isPresent());
        assertEquals("супер вещь", booking.get().getItem().getDescription());
    }
}