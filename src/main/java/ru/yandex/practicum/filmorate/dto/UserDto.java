package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
public class UserDto {
    Integer id;
    @Email String email;
    @NotBlank String login;
    String name;
    @Past LocalDate birthday;
    List<Integer> friends;

    public List<Integer> getFriends() {
        if (friends == null) {
            return new ArrayList<>();
        } else {
            return friends;
        }
    }
}
