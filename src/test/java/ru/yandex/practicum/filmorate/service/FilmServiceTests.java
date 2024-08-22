package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

public class FilmServiceTests {
    private FilmService service;

    @BeforeEach
    public void createNewService() {
        service = new FilmService();
    }

    @Test
    public void shouldWeAddLike() {
        User user = User.builder()
                .id(1)
                .name("user")
                .login("user")
                .email("email@email.e")
                .build();
        Film film = Film.builder()
                .id(1)
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();

        service.addLike(user.getId(), film);

        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));
    }

    @Test
    public void shouldWeDoNotAddTwoLikeByOneUser() {
        User user = User.builder()
                .id(1)
                .name("user")
                .login("user")
                .email("email@email.e")
                .build();
        Film film = Film.builder()
                .id(1)
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();

        service.addLike(user.getId(), film);
        service.addLike(user.getId(), film);

        Assertions.assertEquals(film.getLikedUsers().size(), 1);
    }

    @Test
    public void shouldWeDeleteLike() {
        User user = User.builder()
                .id(1)
                .name("user")
                .login("user")
                .email("email@email.e")
                .build();
        Film film = Film.builder()
                .id(1)
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();

        service.addLike(user.getId(), film);
        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));

        service.deleteLike(user.getId(), film);
        Assertions.assertFalse(film.getLikedUsers().contains(user.getId()));
    }

    @Test
    public void shouldWeGetMostPopularFilms() throws NotFoundException, CorruptedDataException {
        FilmStorage storage = new InMemoryFilmStorage();

        for (int i = 0; i < 5; i++) {
            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likesNumber(i)
                    .build();
            storage.addFilm(film);
        }

        List<Film> films = List.of(
                storage.getFilm(5),
                storage.getFilm(4),
                storage.getFilm(3),
                storage.getFilm(2),
                storage.getFilm(1)
        );
        service.setSizeOfTop(5);

        Assertions.assertEquals(service.getMostPopular(storage), films);
    }

    @Test
    public void shouldWeGetMostPopularFilmsWithATopSizeLargerThanTheList() throws NotFoundException,
            CorruptedDataException {
        FilmStorage storage = new InMemoryFilmStorage();

        for (int i = 0; i < 5; i++) {
            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likesNumber(i)
                    .build();
            storage.addFilm(film);
        }

        List<Film> films = List.of(
                storage.getFilm(5),
                storage.getFilm(4),
                storage.getFilm(3),
                storage.getFilm(2),
                storage.getFilm(1)
        );
        service.setSizeOfTop(10);

        Assertions.assertEquals(service.getMostPopular(storage), films);
    }

    @Test
    public void shouldWeGetMostPopularFilmsWithATopSizeSmallerThanTheList() throws NotFoundException,
            CorruptedDataException {
        FilmStorage storage = new InMemoryFilmStorage();

        for (int i = 0; i < 5; i++) {
            Film film = Film.builder()
                    .duration(1)
                    .releaseDate(LocalDate.now())
                    .description("description")
                    .name("film")
                    .likesNumber(i)
                    .build();
            storage.addFilm(film);
        }

        List<Film> films = List.of(
                storage.getFilm(5),
                storage.getFilm(4),
                storage.getFilm(3)
        );
        service.setSizeOfTop(3);

        Assertions.assertEquals(service.getMostPopular(storage), films);
    }
}
