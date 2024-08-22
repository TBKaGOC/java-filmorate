package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Include
    Integer id;
    @Email String email;
    @NotBlank String login;
    String name;
    @Past LocalDate birthday;
    Set<Integer> friends;

    public void addFriend(User user) {
        if (friends == null) {
            friends = new HashSet<>();
        }

        friends.add(user.getId());
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

        return friends;
    }
}
