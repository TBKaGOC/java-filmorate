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

    Integer addUser(User user) throws DuplicatedDataException, NotFoundException;

    void addFriend(User recipient, User sender, Boolean confirmed);

    void updateUser(User user) throws DuplicatedDataException;

    void deleteUser(Integer id);

    void deleteFriend(Integer recipient, Integer sender) throws NotFoundException;

    boolean contains(Integer id);
}
