package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findAllByItem_Owner_Id(Long userId);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStart(Long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndStatusAndEndBeforeOrderByEnd(Long id, Status status, LocalDateTime start);

    Optional<Booking> findFirstByItem_IdAndEndBeforeAndStatus(Long id, LocalDateTime start, Status status);

}
