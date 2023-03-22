package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserRequestDto {
    private String name;
    @Email
    private String email;
}