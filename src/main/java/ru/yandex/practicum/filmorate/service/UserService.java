package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
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
    private final UserMapper mapper;

    public Collection<UserDto> getUsers() {
        return storage.getUsers().stream().map(mapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto getUser(Integer id) throws NotFoundException {
        if (storage.contains(id)) {
            return mapper.mapToUserDto(storage.getUser(id));
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

        var friends = storage.getFriends(id);

        log.info("Для пользователя {} вернул {} друзей", id, friends.size());

        return friends.stream().map(mapper::mapToUserDto).collect(Collectors.toList());
    }

    public void addUser(UserDto user) throws DuplicatedDataException, NotFoundException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int id = storage.addUser(mapper.mapToUser(user));
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

        var friends = storage.getFriends(sender);

        User senderUser = storage.getUser(sender);
        User recipientUser = storage.getUser(recipient);

        if (!friends.contains(recipient)) {

            storage.addFriend(recipientUser, senderUser, false);
        }

        log.info("Для пользователя {} добавлен друг {} ", sender, recipient);

        return List.of(mapper.mapToUserDto(senderUser),
                mapper.mapToUserDto(recipientUser));
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
            return mapper.mapToUserDto(oldUser);
        } else {
            log.warn("Не удалось обновить пользователя {}", user.getId());
            throw new NotFoundException("Пользователь " + user.getId() + " не найден");
        }
    }

    public void deleteUser(Integer userId) {
        storage.deleteUser(userId);
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
        storage.deleteFriend(recipient, sender);
        log.info("Для пользователя {} удален друг {} ", sender, recipient);
    }

    public Set<UserDto> getMutualFriend(Integer user1, Integer user2) throws NotFoundException {
        var friends = storage.getMutualFriend(user1, user2);

        log.info("Для пользователей {}, {} вернул {} друзей", user1, user2, friends.size());

        return friends.stream().map(mapper::mapToUserDto).collect(Collectors.toSet());
    }

    public Collection<FeedDto> getFeeds(int userId) throws NotFoundException {
        log.info("Request to get feeds userId {}", userId);

        if (!storage.contains(userId)) {
            log.warn("Не удалось найти пользователя {}", userId);
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }

        return storage.getFeeds(userId)
                .stream()
                .map(FeedMapper::mapToFeedDto)
                .toList();
    }
}
