package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
public class User {
    @EqualsAndHashCode.Include Integer id;
    @Email String email;
    @NotBlank String login;
    String name;
    @Past LocalDate birthday;
}
