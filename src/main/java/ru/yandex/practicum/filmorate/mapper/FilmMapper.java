package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmMapper {
    public FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likedUsers(film.getLikedUsers())
                .genres(film.getGenres())
                .mpa(film.getRating())
                .directors(film.getDirectors())
                .build();
    }

    public Film mapToFilm(FilmDto film) {
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likedUsers(film.getLikedUsers())
                .genres(film.getGenres())
                .rating(film.getMpa())
                .directors(film.getDirectors())
                .build();
    }
}
