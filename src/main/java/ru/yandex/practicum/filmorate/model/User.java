package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class User {
    Integer id;
    @Email String email;
    @NotBlank String login;
    String name;
    @Past LocalDate birthday;
    Map<Integer, Boolean> friends;

    public void addFriend(User user, boolean confirmed) {
        if (friends == null) {
            friends = new HashMap<>();
        }

        friends.put(user.getId(), confirmed);
    }

    public void deleteFriend(User user) {
        if (friends != null) {
            friends.remove(user.getId());
        }
    }

    public Set<Integer> getFriends() {
        if (friends == null) {
            return new HashSet<>();
        }

        return friends.keySet();
    }

    public boolean isFriendConfirm(Integer friendId) {
        return friends.get(friendId);
    }
}
