package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserMapper {
    public UserDto mapToUserDto(User user) {
        Map<Integer, Boolean> friends = new HashMap<>();

        for (Integer friendId : user.getFriends()) {
            friends.put(friendId, user.isFriendConfirm(friendId));
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(friends.keySet().stream().toList())
                .build();
    }

    public User mapToUser(UserDto user) {
        Map<Integer, Boolean> result = new HashMap<>();

        for (Integer id: user.getFriends()) {
            result.put(id, true);
        }

        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(result)
                .build();
    }
}
