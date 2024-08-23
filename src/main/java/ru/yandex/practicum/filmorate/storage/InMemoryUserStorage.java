package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

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
            log.warn("Не удалось получить пользователя {}", id);
            throw new NotFoundException("Пользователь " + id + " не найден");
        }

        return users.get(id);
    }

    @Override
    public Collection<User> getFriends(Integer id) throws NotFoundException {
        if (!users.containsKey(id)) {
            log.warn("Не удалось получить друзей пользователя {}", id);
            throw new NotFoundException("Пользователь " + id + " не найден");
        }

        Collection<User> result = new HashSet<>();
        for (Integer friend: users.get(id).getFriends()) {
            result.add(getUser(friend));
        }
        return result;
    }

    @Override
    public Set<User> getMutualFriend(Integer id1, Integer id2) throws NotFoundException {
        User user1 = getUser(id1);
        User user2 = getUser(id2);

        Set<Integer> friends = user1.getFriends().stream()
                .filter(e -> user2.getFriends().contains(e))
                .collect(Collectors.toSet());

        Set<User> result = new HashSet<>();
        for (Integer id: friends) {
            result.add(getUser(id));
        }

        return result;
    }

    @Override
    public void addUser(User user) throws DuplicatedDataException {
        for (User user1: users.values()) {
            if (StringUtils.equals(user.getEmail(), user1.getEmail())) {
                log.warn("Не удалось добавить нового пользователя");
                throw new DuplicatedDataException("Email " + user.getEmail() + " уже используется");
            } else if (StringUtils.equals(user.getLogin(), user1.getLogin())) {
                log.warn("Не удалось добавить нового пользователя");
                throw new DuplicatedDataException("Логин " + user.getLogin() + " уже используется");
            }
        }

        if (!StringUtils.isNotBlank(user.getName())) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());

        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean contains(Integer id) {
        return users.containsKey(id);
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
