package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilm(Integer id) throws NotFoundException {
        if (!films.containsKey(id)) {
            log.warn("При попытке найти фильм " + id + " возникает NotFoundException");
            throw new NotFoundException("Фильм " + id + " не найден");
        }

        return films.get(id);
    }

    public void addFilm(Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("При попытке добавить фильм " + film.getId() + " возникает CorruptedDataException");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());

        films.put(film.getId(), film);
        log.info("Новый фильм успешно добавлен");
    }

    public void updateFilm(Film film) throws NotFoundException, CorruptedDataException {
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
                    log.warn("При попытке обновить фильм " + film.getId() + " возникает CorruptedDataException");
                    throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() != null) {
                oldFilm.setDuration(film.getDuration());
            }

            log.info("Фильм " + film.getId() + " успешно обновлён");
        } else {
            log.warn("При попытке обновить фильм " + film.getId() + " возникает NotFoundException");
            throw new NotFoundException("Фильм " + film.getId() + " не найден");
        }
    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
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
