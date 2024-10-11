package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
public class FilmDto {
    Integer id;
    @NotBlank String name;
    @Size(max = 200) String description;
    @Past LocalDate releaseDate;
    @Positive Integer duration;
    private Set<Integer> likedUsers;
    private Rating mpa;
    private Set<Genre> genres;
}
