package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.exception.EmptyPointerException;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    @Column(name = "owner_id")
    private Long owner;
    @Column (name = "request_id")
    private Long request;

    public Item(Long id, @NotEmpty String name, @NotEmpty String description, @NonNull Boolean available, Long owner,
                Long itemRequest) {
        if (Objects.isNull(name) || Objects.isNull(description) || name.isEmpty() || description.isEmpty()) {
            throw new EmptyPointerException("Значение имени или описания не могут быть пустыми");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = itemRequest;
    }

    public Item() {

    }
}
