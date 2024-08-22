package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage storage;
    private final UserService service;

    @GetMapping
    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) throws NotFoundException {
        return storage.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) throws NotFoundException {
        Set<User> result = new HashSet<>();
        for (Integer friendId: storage.getUser(id).getFriends()) {
            result.add(storage.getUser(friendId));
        }
        return result;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable int id, @PathVariable int otherId) throws NotFoundException {
        return service.getMutualFriend(storage.getUser(id), storage.getUser(otherId));
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws DuplicatedDataException {
        storage.addUser(user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws DuplicatedDataException, NotFoundException {
        storage.updateUser(user);
        return storage.getUser(user.getId());
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<User> addFriend(@PathVariable int id, @PathVariable int friendId) throws NotFoundException {
        User sender = storage.getUser(id);
        User recipient = storage.getUser(friendId);

        service.addFriend(sender, recipient);

        return List.of(recipient, sender);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) throws NotFoundException {
        User sender = storage.getUser(id);
        User recipient = storage.getUser(friendId);

        service.deleteFriend(sender, recipient);
    }
}
