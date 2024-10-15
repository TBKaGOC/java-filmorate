package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final FilmMapper mapper;
    private final DirectorDbStorage directorStorage;
    private final RatingDbStorage ratingStorage;
    private final GenreDbStorage genreStorage;

    public Collection<FilmDto> getFilms() throws NotFoundException {
        return storage.getFilms().stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }

    public FilmDto getFilm(int id) throws NotFoundException {

        return mapper.mapToFilmDto(storage.getFilm(id));
    }

    public void addFilm(FilmDto film) throws CorruptedDataException, NotFoundException {
        int id = storage.addFilm(mapper.mapToFilm(film));
        log.info("Успешно добавлен новый фильм {}", id);
        film.setId(id);
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            film.setDirectors(addDirectorsToFilm(film.getId(), film.getDirectors()));
        }
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
            if (film.getMpa() != null) {
                oldFilm.setRating(film.getMpa());
            }
            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                LinkedHashSet<Integer> directors = directorStorage.findDirectorsIdsByFilmId(film.getId());
                oldFilm.setDirectors(addDirectorsToFilm(film.getId(), film.getDirectors().stream()
                        .filter(d -> !directors.contains(d.getId()))
                        .collect(Collectors.toSet())
                ));
            } else {
                storage.deleteDirectorsId(film.getId());
            }
            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                oldFilm.setGenres(film.getGenres());
            }
            storage.updateFilm(oldFilm);
            log.info("Успешно обновлён фильм {}", film.getId());
            return mapper.mapToFilmDto(oldFilm);
        } else {
            log.warn("Не удалось обновить фильм {}", film.getId());
            throw new NotFoundException("Фильм " + film.getId() + " не найден");
        }
    }

    private LinkedHashSet<Director> addDirectorsToFilm(int filmId, Set<Director> inputDirectors)
            throws NotFoundException {
        LinkedHashSet<Director> directors = new LinkedHashSet<>();
        for (Director director : inputDirectors) {
            Director directorStorageDirector = directorStorage.findDirector(director);
            try {
                storage.addDirectorId(filmId, director.getId());
                directors.add(directorStorageDirector);
            } catch (DuplicatedDataException ignored) {
            }
        }
        return directors;
    }

    public void addLike(int likedUser, int film) throws NotFoundException {
        if (!userStorage.contains(likedUser)) {
            log.warn("Не удалось добавить лайк фильму {}", film);
            throw new NotFoundException("Пользователь " + likedUser + " не найден");
        }
        storage.addLike(likedUser, film);
        log.info("Лайк пользоватля {} успешно добавлен фильму {}", likedUser, film);
    }

    public void deleteFilm(int id) {
        storage.deleteFilm(id);
    }

    public void deleteLike(int unlikedUser, int film) throws NotFoundException {
        if (!userStorage.contains(unlikedUser)) {
            log.warn("Не удалось удалить лайк у фильма {}", film);
            throw new NotFoundException("Пользователь " + unlikedUser + " не найден");
        }
        storage.deleteLike(unlikedUser, film);
        log.info("Лайк пользоватля {} успешно удалён у фильма {}", unlikedUser, film);
    }

    public List<FilmDto> getMostPopular(int count, Integer genreId, Integer year) {
        return storage.getMostPopular(count, genreId, year).stream().map(mapper::mapToFilmDto).collect(Collectors.toList());
    }


    public Collection<FilmDto> findDirectorFilms(int directorId, String sortConditions) throws NotFoundException {
        Director director = directorStorage.findDirector(directorId);
        String message = String.format("Получаем список фильмов режиссера %s", director.getName());

        Collection<Film> films;
        if (sortConditions.equals("year")) {
            log.debug(message + " по году выпуска");
            films = storage.findDirectorFilmsOrderYear(directorId);
        } else if (sortConditions.equals("likes")) {
            log.debug(message + " по количеству лайков");
            films = storage.findDirectorFilmsOrderLikes(directorId);
        } else {
            log.debug("Условия сортировки не заданы. " + message);
            films = storage.findDirectorFilms(directorId);
        }

        List<FilmDto> collect = new ArrayList<>();
        for (Film film : films) {
            FilmDto filmDto = fillFilmData(film);
            collect.add(filmDto);
        }
        return collect;
    }

    private FilmDto fillFilmData(Film film) throws NotFoundException {
        log.debug(String.format("Ищем жанры фильма %s", film.getName()));
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        for (Integer i : genreStorage.findGenresIdsByFilmId(film.getId())) {
            try {
                Genre genre = genreStorage.getGenre(i);
                genres.add(genre);
            } catch (NotFoundException ignored) {
            }
        }
        log.debug(String.format("Ищем режиссеров фильма %s", film.getName()));
        LinkedHashSet<Director> directors = new LinkedHashSet<>();
        for (Integer i : directorStorage.findDirectorsIdsByFilmId(film.getId())) {
            try {
                Director director = directorStorage.findDirector(i);
                directors.add(director);
            } catch (NotFoundException ignored) {
            }
        }
        log.debug(String.format("Ищем лайки фильма %s", film.getName()));
        LinkedHashSet<Integer> likes = storage.getLikes(film.getId());
        log.debug(String.format("Ищем рейтинг фильма %s", film.getName()));
        Rating mpa = ratingStorage.getRating(ratingStorage.findRatingIdByFilmId(film.getId()));
        log.debug(String.format("Фильм %s найден!", film.getName()));
        film.setRating(mpa);
        film.setGenres(genres);
        film.setLikedUsers(likes);
        film.setDirectors(directors);
        return mapper.mapToFilmDto(film);
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

    public Collection<FilmDto> search(String query, String by) {
        List<Film> result = new ArrayList<>();

        if (by.contains("director")) {
            result.addAll(storage.searchByDirector(query));
        }
        if (by.contains("title")) {
            result.addAll(storage.searchByTitle(query));
        }

        return result.stream()
                .sorted((e1, e2) -> {
                    if (e1.getLikesNumber() != e2.getLikesNumber()) {
                        return e2.getLikesNumber() - e1.getLikesNumber();
                    } else {
                        return e1.getId() - e2.getId();
                    }
                })
                .map(mapper::mapToFilmDto)
                .collect(Collectors.toList());
    }
}
