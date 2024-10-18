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
    private Integer id;
    @Email private String email;
    @NotBlank private String login;
    private String name;
    @Past private LocalDate birthday;
    private List<Integer> friends;

    public List<Integer> getFriends() {
        if (friends == null) {
            return new ArrayList<>();
        } else {
            return friends;
        }
    }
}
