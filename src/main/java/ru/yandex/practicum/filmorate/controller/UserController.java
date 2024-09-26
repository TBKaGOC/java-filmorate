package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) throws NotFoundException {
        return service.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriends(@PathVariable int id) throws NotFoundException {
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getMutualFriends(@PathVariable int id, @PathVariable int otherId)
            throws NotFoundException {
        return service.getMutualFriend(id, otherId);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) throws DuplicatedDataException, NotFoundException {
        service.addUser(user);
        return user;
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UserDto user) throws DuplicatedDataException, NotFoundException {
        return service.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Collection<UserDto> addFriend(@PathVariable int id, @PathVariable int friendId) throws NotFoundException,
            DuplicatedDataException {
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) throws NotFoundException {
        service.deleteFriend(id, friendId);
    }
}
