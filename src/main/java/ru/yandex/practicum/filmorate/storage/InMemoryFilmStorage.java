package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

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
            log.warn("Не удалось получить фильм {}", id);
            throw new NotFoundException("Фильм " + id + " не найден");
        }

        return films.get(id);
    }

    @Override
    public List<Film> getMostPopular(String count) {
        int sizeOfTop =  Integer.parseInt(count);

        List<Film> resultList = films.values().stream()
                .sorted(Comparator.comparing(Film::getLikesNumber).reversed())
                .toList();
        if (sizeOfTop >= resultList.size()) {
            return resultList;
        } else {
            return resultList.subList(0, sizeOfTop);
        }
    }

    public void addFilm(Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("Не удалось добавить новый фильм");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());

        films.put(film.getId(), film);
    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
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
