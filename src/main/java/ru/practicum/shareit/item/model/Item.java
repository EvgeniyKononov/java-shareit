package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.exception.EmptyPointerException;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NonNull
    private Boolean available;
    private Long owner;

    public Item(Long id, @NotEmpty String name, @NotEmpty String description, @NonNull Boolean available, Long owner) {
        if (Objects.isNull(name) || Objects.isNull(description) || name.isEmpty() || description.isEmpty()) {
            throw new EmptyPointerException("Значение имени или описания не могут быть пустыми");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
