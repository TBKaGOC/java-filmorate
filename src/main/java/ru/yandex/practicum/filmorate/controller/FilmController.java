package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("Фильм не может выйти раньше 28 декабря 1895 года");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Новый фильм успешно добавлен");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException, CorruptedDataException {
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getName() != null) {
                oldFilm.setName(film.getName());
            }
            if (film.getDescription() != null) {
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
                    log.warn("Фильм не может выйти раньше 28 декабря 1895 года");
                    throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() != null) {
                oldFilm.setDuration(film.getDuration());
            }

            log.info("Фильм " + film.getId() + " успешно обновлён");
            return oldFilm;
        } else {
            log.warn("Данный фильм не найден");
            throw new NotFoundException("Данный фильм не найден");
        }
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
