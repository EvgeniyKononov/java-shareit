package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Answer {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

}
