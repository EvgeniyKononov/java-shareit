package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User amend(User user);

    User find(Long id);

    void delete(Long id);

    List<User> getAll();
}
