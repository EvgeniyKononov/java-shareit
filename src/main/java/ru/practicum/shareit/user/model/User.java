package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    @NonNull
    private String name;
    @NonNull
    @Email
    private String email;
}
