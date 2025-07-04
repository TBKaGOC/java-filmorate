package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private final RecommendationsService recommendationsService;

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

    @GetMapping("/{id}/friends/common/{other-id}")
    public Collection<UserDto> getMutualFriends(@PathVariable int id, @PathVariable("other-id") int otherId)
            throws NotFoundException {
        return service.getMutualFriend(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public Collection<FeedDto> getFeeds(@PathVariable int id) throws NotFoundException {
        return service.getFeeds(id);
    }

    @GetMapping("/{id}/recommendations")
    public Collection<FilmDto> getRecommendations(@PathVariable int id) {
        return recommendationsService.getRecommendations(id);
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

    @PutMapping("/{id}/friends/{friend-id}")
    public Collection<UserDto> addFriend(@PathVariable int id, @PathVariable("friend-id") int friendId)
            throws NotFoundException {
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{user-id}")
    public void deleteUser(@PathVariable("user-id") int userId) {
        service.deleteUser(userId);
    }

    @DeleteMapping("/{id}/friends/{friend-id}")
    public void deleteFriend(@PathVariable int id, @PathVariable("friend-id") int friendId) throws NotFoundException {
        service.deleteFriend(id, friendId);
    }
}
