package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getUser().getName(),
                comment.getCreated()
        );
    }

    public static Comment toNewEntity(CommentDto dto, Item item, User user) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                item,
                user,
                LocalDateTime.now());
    }
}
