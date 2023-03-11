package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.Answer;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {

    public static ItemRequest toNewEntity(ItemRequestDto dto, User requestor) {
        return new ItemRequest(
                null,
                dto.getDescription(),
                requestor,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDto toDto(ItemRequest request, List<Answer> answers) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor(),
                request.getCreationDateTime(),
                answers
        );
    }
}
