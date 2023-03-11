package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor", referencedColumnName = "id")
    private User requestor;
    @Column(name = "creation_date_time")
    private LocalDateTime creationDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest request = (ItemRequest) o;
        return Objects.equals(id, request.id) && Objects.equals(description, request.description)
                && Objects.equals(requestor, request.requestor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor);
    }
}
