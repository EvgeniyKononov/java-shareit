package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User save(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    @Override
    public User amend(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User find(Long id) {
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
