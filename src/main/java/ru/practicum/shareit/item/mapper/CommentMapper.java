package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;

public interface CommentMapper {
    Comment toEntity(CommentDto dto);

    CommentDto toDto(Comment comment);

    Comment toNewEntity(CommentDto commentDto, Long itemId, Long userId);
}
