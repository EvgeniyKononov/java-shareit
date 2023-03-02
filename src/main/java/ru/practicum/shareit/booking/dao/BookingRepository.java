package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItem(Long itemId);

    Optional<Booking> findFirstByItemAndStatusAndStartAfterOrderByStart(Long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItemAndStatusAndEndBeforeOrderByEnd(Long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItemAndEndBeforeAndStatus(Long id, LocalDateTime start, Status status);
}
