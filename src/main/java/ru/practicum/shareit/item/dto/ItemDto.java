package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EmptyPointerException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDto {
    private long id;
    @NotNull
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingsInItem lastBooking;
    private BookingsInItem nextBooking;
    private List<CommentDto> comments;

    public ItemDto(Long id, @NotEmpty String name, @NotEmpty String description, @NonNull Boolean available) {
        if (Objects.isNull(name) || Objects.isNull(description) || name.isEmpty() || description.isEmpty()) {
            throw new EmptyPointerException("Значение имени или описания не могут быть пустыми");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;

    }

    @Getter
    @Setter
    public static class BookingsInItem {
        private Long id;
        private Long bookerId;

        public BookingsInItem(Booking booking) {
            this.id = booking.getId();
            this.bookerId = booking.getBooker().getId();
        }
    }

}
