package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public void addFriend(User sender, User recipient) {
        sender.addFriend(recipient);
        recipient.addFriend(sender);
    }

    public void deleteFriend(User sender, User recipient) {
        sender.deleteFriend(recipient);
        recipient.deleteFriend(sender);
    }

    public Set<User> getMutualFriend(User user1, User user2) throws NotFoundException {
        Set<Integer> friends = user1.getFriends().stream()
                .filter(e -> user2.getFriends().contains(e))
                .collect(Collectors.toSet());

        Set<User> result = new HashSet<>();
        for (Integer id: friends) {
            result.add(storage.getUser(id));
        }

        return result;
    }
}
