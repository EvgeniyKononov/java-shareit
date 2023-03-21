package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryIT {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private JpaItemRepository itemRepository;
    @Autowired
    private JpaUserRepository userRepository;

    private User user1;
    private Item item;

    @BeforeEach
    public void addRequest() {
        user1 = userRepository.save(User.builder()
                .name("Name 1")
                .email("name@name.ru")
                .build());
        item = itemRepository.save(Item.builder()
                .name("вещь")
                .description("супер вещь")
                .available(true)
                .owner(user1)
                .build());
        commentRepository.save(Comment.builder()
                .text("комментарий")
                .item(item)
                .user(user1)
                .created(LocalDateTime.now())
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemId_whenInvoke_listCommentsReturn() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertEquals(1, comments.size());
        assertEquals("комментарий", comments.get(0).getText());
    }
}