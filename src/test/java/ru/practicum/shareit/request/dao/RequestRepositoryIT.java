package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryIT {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private JpaUserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void addRequest() {
        LocalDateTime current = LocalDateTime.now();
        user1 = userRepository.save(User.builder()
                .id(1L)
                .name("Name 1")
                .email("name@name.ru")
                .build());
        user2 = userRepository.save(User.builder()
                .id(2L)
                .name("Name 2")
                .email("name2@name.ru")
                .build());
        requestRepository.save(ItemRequest.builder()
                .requestor(user1)
                .description("Request 1")
                .creationDateTime(current)
                .build());
        requestRepository.save(ItemRequest.builder()
                .requestor(user2)
                .description("Request 2")
                .creationDateTime(current.plusDays(1))
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findAllByRequestor_IdOrderByCreationDateTimeDesc_whenInvoke_listRequestReturn() {
        List<ItemRequest> requests = requestRepository
                .findAllByRequestor_IdOrderByCreationDateTimeDesc(user1.getId());

        assertEquals(1, requests.size());
        assertEquals("Request 1", requests.get(0).getDescription());
    }

    @Test
    void findAllByRequestorIdWithoutRequester_whenInvoke_pageRequestReturn() {

        Page<ItemRequest> pageRequest = requestRepository
                .findAllByRequestorIdWithoutRequester(user1.getId(), PageRequest.of(0, 10));
        List<ItemRequest> requests = pageRequest.getContent();

        assertEquals(1, requests.size());
        assertEquals("Request 2", requests.get(0).getDescription());
    }
}