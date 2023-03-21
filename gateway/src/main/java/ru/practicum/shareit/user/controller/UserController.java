package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;


    @GetMapping("/{id}")
    public ResponseEntity<Object> find(@PathVariable Long id) {
        log.info("Find user, userId={}", id);
        return userClient.findUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Find all users");
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> amend(@Valid @RequestBody UserRequestDto userDto, @PathVariable Long id) {
        log.info("Updating user {}, userId = {}", userDto, id);
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info("Delete user, userId={}", id);
        return userClient.deleteUser(id);
    }
}

