package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws DuplicatedDataException {
        for (User user1: users.values()) {
            if (user.getEmail().equals(user1.getEmail())) {
                log.warn("Этот email уже используется");
                throw new DuplicatedDataException("Этот email уже используется");
            } else if (user.getLogin().equals(user1.getLogin())) {
                log.warn("Этот логин уже используется");
                throw new DuplicatedDataException("Этот логин уже используется");
            }
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());

        users.put(user.getId(), user);

        log.info("Новый пользователь успешно добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws DuplicatedDataException, NotFoundException {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail() != null) {
                for (User user1: users.values()) {
                    if (user.getEmail().equals(user1.getEmail()) && !user.getId().equals(user1.getId())) {
                        log.warn("Этот email уже используется");
                        throw new DuplicatedDataException("Этот email уже используется");
                    }
                }
                oldUser.setEmail(user.getEmail());
            }

            if (user.getBirthday() != null) {
                oldUser.setBirthday(user.getBirthday());
            }

            if (user.getName() != null) {
                if (!user.getName().isBlank()) {
                    oldUser.setName(user.getName());
                } else {
                    oldUser.setName(user.getLogin());
                }
            } else if (oldUser.getLogin().equals(oldUser.getName())) {
                oldUser.setName(user.getLogin());
            }

            if (user.getLogin() != null) {
                for (User user1: users.values()) {
                    if (user.getLogin().equals(user1.getLogin()) && !user.getId().equals(user1.getId())) {
                        log.warn("Этот логин уже используется");
                        throw new DuplicatedDataException("Этот логин уже используется");
                    }
                }
                oldUser.setLogin(user.getLogin());
            }

            log.info("Пользователь " + user.getId() + " успешно обновлён");
            return oldUser;
        } else {
            log.warn("Данный пользователь не найден");
            throw new NotFoundException("Данный пользователь не найден");
        }
    }

    private int getNextId() {
        int currentMaxId = (int) users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
