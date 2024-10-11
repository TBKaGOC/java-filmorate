package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final FilmMapper mapper;

    public Collection<FilmDto> getFilms() throws NotFoundException {
        return storage.getFilms().stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }

    public FilmDto getFilm(int id) throws NotFoundException {
        log.trace("Request to getFilm by id = " + id);

        var result = mapper.mapToFilmDto(storage.getFilm(id));

        log.trace("Result of getFilm = " + result);

        return result;
    }

    public void addFilm(FilmDto film) throws CorruptedDataException, NotFoundException {
        int id = storage.addFilm(mapper.mapToFilm(film));
        film.setId(id);
        log.info("Успешно добавлен новый фильм {}", film);
    }

    public FilmDto updateFilm(FilmDto film) throws NotFoundException, CorruptedDataException {
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

            return mapper.mapToFilmDto(oldFilm);
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
        storage.addLike(likedUser, film);
        log.info("Лайк пользоватля {} успешно добавлен фильму {}", likedUser, film);
    }

    public void deleteLike(int unlikedUser, int film) throws NotFoundException {
        if (!userStorage.contains(unlikedUser)) {
            log.warn("Не удалось удалить лайк у фильма {}", film);
            throw new NotFoundException("Пользователь " + unlikedUser + " не найден");
        }
        storage.deleteLike(unlikedUser, film);
        log.info("Лайк пользоватля {} успешно удалён у фильма {}", unlikedUser, film);
    }

    public List<FilmDto> getMostPopular(String count) {
        return storage.getMostPopular(count).stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }

    public Collection<FilmDto> getCommonFilms(int userId, int friendId) {
        var result = storage.getCommonFilms(userId, friendId)
                .stream()
                .map(mapper::mapToFilmDto)
                .collect(Collectors.toList());

        log.trace(String.format("getCommonFilms: found %d rows ", result.size()));

        for (var film: result) {
            log.trace("getCommonFilms: found " + film);
        }

        return result;
    }
}
