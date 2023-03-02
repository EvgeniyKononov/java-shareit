package ru.practicum.shareit.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.model.User;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    User findByName(String name);
}
