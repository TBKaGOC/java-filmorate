package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film getFilm(Integer id) throws NotFoundException;

    void addFilm(Film film) throws CorruptedDataException;

    void updateFilm(Film film) throws NotFoundException, CorruptedDataException;

    void deleteFilm(Integer id);
}
