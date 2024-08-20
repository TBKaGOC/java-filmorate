package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class FilmControllerTests {
    private FilmController filmController;

    @BeforeEach
    public void createNewFilmController() {
        filmController = new FilmController();
    }

    @Test
    public void shouldWeGetAllFilms() throws CorruptedDataException {
        Collection<Film> userCollection = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Film newFilm = Film.builder()
                    .name("name")
                    .description("description")
                    .releaseDate(LocalDate.of(2000, 1, 1))
                    .duration(120)
                    .build();

            filmController.createFilm(newFilm);
            userCollection.add(newFilm);
        }

        Assertions.assertTrue(filmController.getFilms().containsAll(userCollection));
    }

    @Test
    public void shouldWeCreateNewFilm() throws CorruptedDataException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();

        Film createdFilm = filmController.createFilm(newFilm);
        newFilm.setId(createdFilm.getId());

        Assertions.assertEquals(newFilm, createdFilm);
    }

    @Test
    public void shouldWeGetExceptionWhenLocalDateIsBeforeEarlyDate() {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrowsExactly(CorruptedDataException.class, () -> filmController.createFilm(newFilm));
    }

    @Test
    public void shouldWeUpdateFilm() throws CorruptedDataException, NotFoundException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        filmController.createFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        Film updateFilm = filmController.updateFilm(newFilm2);

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
        filmController.createFilm(newFilm);

        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(CorruptedDataException.class, () -> filmController.updateFilm(newFilm2));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithNewId() throws CorruptedDataException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        filmController.createFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId() + 1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> filmController.updateFilm(newFilm2));
    }
}
