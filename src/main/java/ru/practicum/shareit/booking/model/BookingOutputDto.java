package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class BookingOutputDto {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private Status status;
    private User booker;
    private Item item;

    public State getState(BookingOutputDto dto) {
        switch (dto.getStatus()) {
            case REJECTED:
            case CANCELLED:
                return State.REJECTED;
            case WAITING:
            case APPROVED:
                return chooseTime(dto);
        }
        return null;
    }

    private State chooseTime(BookingOutputDto dto) {
        if (dto.getStart().isAfter(LocalDateTime.now())) {
            return State.FUTURE;
        } else if (dto.getEnd().isBefore(LocalDateTime.now().plusMinutes(10))) {
            return State.PAST;
        } else {
            return State.CURRENT;
        }
    }
}
