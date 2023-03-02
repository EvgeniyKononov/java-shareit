package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.user.dao.JpaUserRepository;

import java.time.LocalDateTime;

@Component
public class CommentMapperImpl implements CommentMapper {
    private final JpaUserRepository userRepository;

    @Autowired
    public CommentMapperImpl(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Comment toEntity(CommentDto dto) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                dto.getItemId(),
                userRepository.findByName(dto.getAuthorName()).getId(),
                dto.getCreated()
        );
    }

    @Override
    public CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItemId(),
                userRepository.findById(comment.getAuthorId()).get().getName(),
                comment.getCreated()
        );
    }

    @Override
    public Comment toNewEntity(CommentDto dto, Long itemId, Long userId) {
        return new Comment(
                dto.getId(),
                dto.getText(),
                itemId,
                userId,
                LocalDateTime.now());
    }
}
