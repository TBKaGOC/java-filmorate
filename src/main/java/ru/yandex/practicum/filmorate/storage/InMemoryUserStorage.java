package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getUser(Integer id) throws NotFoundException {
        if (!users.containsKey(id)) {
            log.warn("При попытке найти пользователя " + id + " возникает NotFoundException");
            throw new NotFoundException("Пользователь " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public void addUser(User user) throws DuplicatedDataException {
        for (User user1: users.values()) {
            if (StringUtils.equals(user.getEmail(), user1.getEmail())) {
                log.warn("При попытке обновить email пользователя " + user.getId() +
                        " возникает DuplicatedDataException");
                throw new DuplicatedDataException("Email " + user.getEmail() + " уже используется");
            } else if (StringUtils.equals(user.getLogin(), user1.getLogin())) {
                log.warn("При попытке обновить email пользователя " + user.getId() +
                        " возникает DuplicatedDataException");
                throw new DuplicatedDataException("Логин " + user.getLogin() + " уже используется");
            }
        }

        if (!StringUtils.isNotBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());

        users.put(user.getId(), user);

        log.info("Новый пользователь успешно добавлен");
    }

    @Override
    public void updateUser(User user) throws DuplicatedDataException, NotFoundException {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail() != null) {
                for (User user1: users.values()) {
                    if (StringUtils.equals(user.getEmail(), user1.getEmail())) {
                        log.warn("При попытке обновить email пользователя " + user.getId() +
                                " возникает DuplicatedDataException");
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
            } else if (StringUtils.equals(oldUser.getLogin(), oldUser.getName())) {
                oldUser.setName(user.getLogin());
            }

            if (user.getLogin() != null) {
                for (User user1: users.values()) {
                    if (StringUtils.equals(user.getLogin(), user1.getLogin())) {
                        log.warn("При попытке обновить логин пользователя " + user.getId() +
                                " возникает DuplicatedDataException");
                        throw new DuplicatedDataException("Логин " + user.getLogin() + " уже используется");
                    }
                }
                oldUser.setLogin(user.getLogin());
            }

            log.info("Пользователь " + user.getId() + " успешно обновлён");
        } else {
            log.warn("При попытке обновить пользователя " + user.getId() + " возникает NotFoundException");
            throw new NotFoundException("Пользователь " + user.getId() + " не найден");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    public void findUser(Integer id) throws NotFoundException {
        if (!users.containsKey(id)) {
            log.warn("Пользователь " + id + " не найден");
            throw new NotFoundException("Пользователь " + id + " не найден");
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
