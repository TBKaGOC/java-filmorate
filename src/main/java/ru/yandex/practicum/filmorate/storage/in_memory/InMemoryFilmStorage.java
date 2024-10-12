package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilm(Integer id) throws NotFoundException {
        if (!films.containsKey(id)) {
            log.warn("Не удалось получить фильм {}", id);
            throw new NotFoundException("Фильм " + id + " не найден");
        }

        return films.get(id);
    }

    @Override
    public List<Film> getMostPopular(int count, Integer genreId, Integer year) {
        List<Film> resultList = films.values().stream().toList();
        if (genreId != null && year != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        } else if (genreId != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
                    .toList();
        } else if (year != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        }
        return resultList.stream()
                .sorted(Comparator.comparing(Film::getLikesNumber).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Integer addFilm(Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("Не удалось добавить новый фильм");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());

        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
    }

    @Override
    public void addLike(int likedUser, int film) throws NotFoundException {
        getFilm(film).addLike(likedUser);
    }

    @Override
    public void deleteLike(int unlikedUser, int film) throws NotFoundException {
        getFilm(film).deleteLike(unlikedUser);
    }

    @Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }

    private int getNextId() {
        int currentMaxId = (int) films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
