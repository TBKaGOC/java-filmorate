package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@ToString
public class FilmDto {
    private Integer id;
    @NotBlank private String name;
    @Size(max = 200) private String description;
    private LocalDate releaseDate;
    @Positive private Integer duration;
    private Set<Integer> likedUsers;
    private Rating mpa;
    private Set<Genre> genres;
    private LinkedHashSet<Director> directors;
}
