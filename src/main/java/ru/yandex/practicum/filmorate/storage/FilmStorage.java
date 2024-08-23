package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilm(Integer id) throws NotFoundException;

    List<Film> getMostPopular(String count);

    void addFilm(Film film) throws CorruptedDataException;

    void deleteFilm(Integer id);

    boolean contains(Integer id);
}
