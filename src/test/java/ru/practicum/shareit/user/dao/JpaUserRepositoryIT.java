package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class JpaUserRepositoryIT {

    @Autowired
    private JpaUserRepository userRepository;

    @Test
    void setUserRepository() {
        userRepository.findAll();
    }
}