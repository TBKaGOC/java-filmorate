package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User getUser(Integer id) throws NotFoundException;

    void addUser(User user) throws DuplicatedDataException;

    void updateUser(User user) throws DuplicatedDataException, NotFoundException;

    void deleteUser(Integer id);

    void findUser(Integer id) throws NotFoundException;
}
