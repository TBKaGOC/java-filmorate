package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public Film getFilm(int id) throws NotFoundException {
        return storage.getFilm(id);
    }

    public void addFilm(Film film) throws CorruptedDataException {
        storage.addFilm(film);
        log.info("Успешно добавлен новый фильм {}", film.getId());
    }

    public void updateFilm(Film film) throws NotFoundException, CorruptedDataException {
        if (storage.contains(film.getId())) {
            Film oldFilm = storage.getFilm(film.getId());
            if (film.getName() != null) {
                oldFilm.setName(film.getName());
            }
            if (film.getDescription() != null) {
                oldFilm.setDescription(film.getDescription());
            }
            if (film.getReleaseDate() != null) {
                if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
                    log.warn("Не удалось обновить фильм {}", film.getId());
                    throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
                }
                oldFilm.setReleaseDate(film.getReleaseDate());
            }
            if (film.getDuration() != null) {
                oldFilm.setDuration(film.getDuration());
            }

            log.info("Успешно обновлён фильм {}", film.getId());
        } else {
            log.warn("Не удалось обновить фильм {}", film.getId());
            throw new NotFoundException("Фильм " + film.getId() + " не найден");
        }
    }

    public void addLike(int likedUser, int film) throws NotFoundException {
        if (!userStorage.contains(likedUser)) {
            log.warn("Не удалось добавить лайк фильму {}", film);
            throw new NotFoundException("Пользователь " + likedUser + " не найден");
        }
        storage.getFilm(film).addLike(likedUser);
        log.info("Лайк пользоватля {} успешно добавлен фильму {}", likedUser, film);
    }

    public void deleteLike(int unlikedUser, int film) throws NotFoundException {
        if (!userStorage.contains(unlikedUser)) {
            log.warn("Не удалось удалить лайк у фильма {}", film);
            throw new NotFoundException("Пользователь " + unlikedUser + " не найден");
        }
        storage.getFilm(film).deleteLike(unlikedUser);
        log.info("Лайк пользоватля {} успешно удалён у фильма {}", unlikedUser, film);
    }

    public List<Film> getMostPopular(String count) {
        return storage.getMostPopular(count);
    }
}
