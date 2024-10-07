package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;

public class FilmStorageTests {
    private FilmStorage filmStorage;

    @BeforeEach
    public void createNewFilmController() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    public void shouldWeGetAllFilms() throws CorruptedDataException, NotFoundException {
        Collection<Film> filmCollection = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Film newFilm = Film.builder()
                    .name("name")
                    .description("description")
                    .releaseDate(LocalDate.of(2000, 1, 1))
                    .duration(120)
                    .build();

            filmStorage.addFilm(newFilm);
            filmCollection.add(newFilm);
        }

        Assertions.assertTrue(filmStorage.getFilms().containsAll(filmCollection));
    }

    @Test
    public void shouldWeCreateNewFilm() throws CorruptedDataException, NotFoundException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
        filmStorage.addFilm(newFilm);
        newFilm.setId(1);

        Assertions.assertEquals(newFilm, filmStorage.getFilm(1));
    }

    @Test
    public void shouldWeGetExceptionWhenLocalDateIsBeforeEarlyDate() {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrowsExactly(CorruptedDataException.class, () -> filmStorage.addFilm(newFilm));
    }

    @Test
    public void shouldWeGetMostPopularFilms() throws NotFoundException, CorruptedDataException {
        for (int i = 0; i < 5; i++) {
            Set<Integer> likedUser = new HashSet<>();

            for (int j = 0; j < i; j++) {
                likedUser.add(j);
            }

            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likedUsers(likedUser)
                    .build();
            filmStorage.addFilm(film);
        }

        List<Film> films = List.of(
                filmStorage.getFilm(5),
                filmStorage.getFilm(4),
                filmStorage.getFilm(3),
                filmStorage.getFilm(2),
                filmStorage.getFilm(1)
        );

        Assertions.assertEquals(filmStorage.getMostPopular("5"), films);
    }

    @Test
    public void shouldWeGetMostPopularFilmsWithATopSizeLargerThanTheList() throws NotFoundException,
            CorruptedDataException {
        for (int i = 0; i < 5; i++) {
            Set<Integer> likedUser = new HashSet<>();

            for (int j = 0; j < i; j++) {
                likedUser.add(j);
            }

            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likedUsers(likedUser)
                    .build();
            filmStorage.addFilm(film);
        }

        List<Film> films = List.of(
                filmStorage.getFilm(5),
                filmStorage.getFilm(4),
                filmStorage.getFilm(3),
                filmStorage.getFilm(2),
                filmStorage.getFilm(1)
        );

        Assertions.assertEquals(filmStorage.getMostPopular("10"), films);
    }

    @Test
    public void shouldWeGetMostPopularFilmsWithATopSizeSmallerThanTheList() throws NotFoundException,
            CorruptedDataException {
        for (int i = 0; i < 5; i++) {
            Set<Integer> likedUser = new HashSet<>();

            for (int j = 0; j < i; j++) {
                likedUser.add(j);
            }

            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likedUsers(likedUser)
                    .build();
            filmStorage.addFilm(film);
        }

        List<Film> films = List.of(
                filmStorage.getFilm(5),
                filmStorage.getFilm(4),
                filmStorage.getFilm(3)
        );

        Assertions.assertEquals(filmStorage.getMostPopular("3"), films);
    }
}
