package ru.practicum.shareit.booking.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dao.JpaItemRepository;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.constant.Constant.NOT_FOUND_ITEM_ID;
import static ru.practicum.shareit.constant.Constant.NOT_FOUND_USER_ID;


@Component
public class BookingMapperImpl implements BookingMapper {
    private final JpaUserRepository userRepository;
    private final JpaItemRepository itemRepository;

    @Autowired
    public BookingMapperImpl(JpaUserRepository userRepository,
                             JpaItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking toNewEntity(Long bookerId, BookingDto dto) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID));
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                dto.getItemId(),
                bookerId,
                Status.WAITING
        );
    }

    @Override
    public Booking toEntity(BookingDto dto) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                dto.getItemId(),
                dto.getBookerId(),
                dto.getStatus()
        );
    }

    @Override
    public BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBookerId(),
                booking.getStatus(),
                null
        );
    }

    @Override
    public BookingOutputDto toOutputDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                userRepository.findById(booking.getBookerId())
                        .orElseThrow(() -> new NotFoundUserException(NOT_FOUND_USER_ID)),
                itemRepository.findById(booking.getItem())
                        .orElseThrow(() -> new NotFoundItemException(NOT_FOUND_ITEM_ID))
        );
    }
}
