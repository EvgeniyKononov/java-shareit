package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.JpaUserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JpaItemRepositoryIT {

    @Autowired
    private JpaItemRepository itemRepository;
    @Autowired
    private JpaUserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    public void addRequest() {
        user1 = userRepository.save(User.builder()
                .name("Name 1")
                .email("name@name.ru")
                .build());
        user2 = userRepository.save(User.builder()
                .name("Name 2")
                .email("name2@name.ru")
                .build());
        item = itemRepository.save(Item.builder()
                .name("вещь")
                .description("супер вещь")
                .available(true)
                .owner(user1)
                .build());
        itemRepository.save(Item.builder()
                .name("печь")
                .description("супер печь")
                .available(true)
                .owner(user2)
                .build());
    }

    @AfterEach
    public void deleteRequest() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        requestRepository.deleteAll();
    }

    @Test
    void findAllByOwner_Id_whenInvoke_listItemsReturn() {
        List<Item> items = itemRepository.findAllByOwner_Id(user1.getId());

        assertEquals(1, items.size());
        assertEquals("супер вещь", items.get(0).getDescription());
    }

    @Test
    void findAllByOwner_IdOrderById_whenInvoke_listItemsReturn() {
        itemRepository.save(Item.builder()
                .name("диск")
                .description("диск для колеса")
                .available(true)
                .owner(user1)
                .build());

        List<Item> items = itemRepository.findAllByOwner_Id(user1.getId());

        assertEquals(2, items.size());
        assertEquals("супер вещь", items.get(0).getDescription());
        assertEquals("диск для колеса", items.get(1).getDescription());
    }

    @Test
    void findAllByRequest_Id_whenInvoke_listItemsReturn() {
        LocalDateTime current = LocalDateTime.now();
        ItemRequest request = requestRepository.save(ItemRequest.builder()
                .requestor(user1)
                .description("Request 1")
                .creationDateTime(current)
                .build());
        item.setRequest(request);
        itemRepository.save(item);

        List<Item> items = itemRepository.findAllByRequest_Id(request.getId());

        assertEquals(1, items.size());
        assertEquals("супер вещь", items.get(0).getDescription());
    }
}