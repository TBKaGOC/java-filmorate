package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> getUsers();

    User getUser(Integer id) throws NotFoundException;

    Collection<User> getFriends(Integer id) throws NotFoundException;

    Set<User> getMutualFriend(Integer id1, Integer id2) throws NotFoundException;

    void addUser(User user) throws DuplicatedDataException;

    void deleteUser(Integer id);

    boolean contains(Integer id);
}
