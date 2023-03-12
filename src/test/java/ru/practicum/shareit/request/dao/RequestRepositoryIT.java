package ru.practicum.shareit.request.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RequestRepositoryIT {

    @Autowired
    private RequestRepository requestRepository;


    @Test
    void findAllByRequestor_IdOrderByCreationDateTimeDesc() {
    }

    @Test
    void findAllByRequestorIdWithoutRequester() {
    }
}