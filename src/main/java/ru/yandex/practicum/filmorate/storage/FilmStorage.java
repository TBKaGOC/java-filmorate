package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms() throws NotFoundException;

    Film getFilm(Integer id) throws NotFoundException;

    List<Film> getMostPopular(int count, Integer genreId, Integer year);

    Integer addFilm(Film film) throws CorruptedDataException, NotFoundException;

    void updateFilm(Film film);

    void deleteFilm(Integer id);

    void addLike(int likedUser, int film) throws NotFoundException;

    void deleteLike(int unlikedUser, int film) throws NotFoundException;

    boolean contains(Integer id);
}
