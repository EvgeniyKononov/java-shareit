package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserRequestDto {
    @NonNull
    private String name;
    @NonNull
    @Email
    private String email;
}