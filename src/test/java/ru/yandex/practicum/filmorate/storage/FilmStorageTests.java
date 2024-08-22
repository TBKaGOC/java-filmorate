package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class FilmStorageTests {
    private FilmStorage filmStorage;

    @BeforeEach
    public void createNewFilmController() {
        filmStorage = new InMemoryFilmStorage();
    }

    @Test
    public void shouldWeGetAllFilms() throws CorruptedDataException {
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
    public void shouldWeUpdateFilm() throws CorruptedDataException, NotFoundException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        filmStorage.addFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        filmStorage.updateFilm(newFilm2);
        Film updateFilm = filmStorage.getFilm(newFilm2.getId());

        Assertions.assertEquals(newFilm2, updateFilm);
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateLocalDateIsBeforeEarlyDate() throws CorruptedDataException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        filmStorage.addFilm(newFilm);

        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(CorruptedDataException.class, () -> filmStorage.updateFilm(newFilm2));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithNewId() throws CorruptedDataException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        filmStorage.addFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId() + 1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(newFilm2));
    }
}
