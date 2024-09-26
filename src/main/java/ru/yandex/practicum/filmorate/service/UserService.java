package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage storage;

    public Collection<UserDto> getUsers() {
        return storage.getUsers().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto getUser(Integer id) throws NotFoundException {
        if (storage.contains(id)) {
            return UserMapper.mapToUserDto(storage.getUser(id));
        } else {
            log.warn("Не удалось получить пользователя {}", id);
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
    }

    public Collection<UserDto> getFriends(Integer id) throws NotFoundException {
        if (!storage.contains(id)) {
            log.warn("Не удалось получить друзей пользователя {}", id);
            throw new NotFoundException("Пользователь " + id + " не найден");
        }
        return storage.getFriends(id).stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public void addUser(UserDto user) throws DuplicatedDataException, NotFoundException {
        int id = storage.addUser(UserMapper.mapToUser(user));
        log.info("Успешно добавлен новый пользователь {}", id);
        user.setId(id);
    }

    public Collection<UserDto> addFriend(Integer sender, Integer recipient) throws NotFoundException {
        if (!storage.contains(sender)) {
            log.warn("Не удалось отправить заявку пользователя {}", sender);
            throw new NotFoundException("Пользователь " + sender + " не найден");
        }
        if (!storage.contains(recipient)) {
            log.warn("Не удалось отправить заявку пользователю {}", recipient);
            throw new NotFoundException("Пользователь " + recipient + " не найден");
        }
        User senderUser = storage.getUser(sender);
        User recipientUser = storage.getUser(recipient);

        if (senderUser.getFriends().contains(recipient)) {
            recipientUser.addFriend(senderUser, true);
            senderUser.addFriend(recipientUser, true);
            storage.addFriend(recipientUser, senderUser, true);
        } else {
            recipientUser.addFriend(senderUser, false);
            storage.addFriend(senderUser, recipientUser, false);
        }
        return List.of(UserMapper.mapToUserDto(senderUser), UserMapper.mapToUserDto(recipientUser));
    }

    public UserDto updateUser(UserDto user) throws NotFoundException, DuplicatedDataException {
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

            storage.updateUser(oldUser);

            log.info("Пользователь " + user.getId() + " успешно обновлён");
            return UserMapper.mapToUserDto(oldUser);
        } else {
            log.warn("Не удалось обновить пользователя {}", user.getId());
            throw new NotFoundException("Пользователь " + user.getId() + " не найден");
        }
    }


    public void deleteFriend(Integer sender, Integer recipient) throws NotFoundException {
        if (!storage.contains(sender)) {
            log.warn("Не удалось удалить друга пользователя {}", sender);
            throw new NotFoundException("Пользователь " + sender + " не найден");
        }
        if (!storage.contains(recipient)) {
            log.warn("Не удалось пользователя {} из друзей пользователя {}", recipient, sender);
            throw new NotFoundException("Пользователь " + recipient + " не найден");
        }
        User user1 = storage.getUser(sender);
        User user2 = storage.getUser(recipient);
        user1.deleteFriend(user2);
        user2.deleteFriend(user1);
        storage.deleteFriend(sender, recipient);
        log.info("Друзьями успешно перестали быть пользователи {} и {}", sender, recipient);
    }

    public Set<UserDto> getMutualFriend(Integer user1, Integer user2) throws NotFoundException {
        return storage.getMutualFriend(user1, user2).stream().map(UserMapper::mapToUserDto).collect(Collectors.toSet());
    }
}
