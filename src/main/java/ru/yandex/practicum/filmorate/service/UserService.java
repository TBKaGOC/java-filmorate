package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage storage;

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User getUser(Integer id) throws NotFoundException {
        return storage.getUser(id);
    }

    public Collection<User> getFriends(Integer id) throws NotFoundException {
        return storage.getFriends(id);
    }

    public void addUser(User user) throws DuplicatedDataException {
        storage.addUser(user);
        log.info("Успешно добавлен новый пользователь {}", user.getId());
    }

    public void addFriend(Integer sender, Integer recipient) throws NotFoundException {
        User user1 = storage.getUser(sender);
        User user2 = storage.getUser(recipient);
        user1.addFriend(user2);
        user2.addFriend(user1);
        log.info("Друзьями успешно стали пользователи {} и {}", sender, recipient);
    }

    public void updateUser(User user) throws NotFoundException, DuplicatedDataException {
        if (storage.contains(user.getId())) {
            User oldUser = storage.getUser(user.getId());
            if (user.getEmail() != null) {
                for (User user1 : storage.getUsers()) {
                    if (StringUtils.equals(user.getEmail(), user1.getEmail())) {
                        log.warn("Не удалось обновить пользователя {}", user.getId());
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
                for (User user1 : storage.getUsers()) {
                    if (StringUtils.equals(user.getLogin(), user1.getLogin())) {
                        log.warn("Не удалось обновить пользователя {}", user.getId());
                        throw new DuplicatedDataException("Логин " + user.getLogin() + " уже используется");
                    }
                }
                oldUser.setLogin(user.getLogin());
            }

            log.info("Пользователь " + user.getId() + " успешно обновлён");
        } else {
            log.warn("Не удалось обновить пользователя {}", user.getId());
            throw new NotFoundException("Пользователь " + user.getId() + " не найден");
        }
    }


    public void deleteFriend(Integer sender, Integer recipient) throws NotFoundException {
        User user1 = storage.getUser(sender);
        User user2 = storage.getUser(recipient);
        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
        log.info("Друзьями успешно перестали быть пользователи {} и {}", sender, recipient);
    }

    public Set<User> getMutualFriend(Integer user1, Integer user2) throws NotFoundException {
        return storage.getMutualFriend(user1, user2);
    }
}
