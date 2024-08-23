package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

public class FilmServiceTests {
    private FilmService service;
    private UserStorage storage;

    @BeforeEach
    public void createNewService() {
        storage = new InMemoryUserStorage();
        service = new FilmService(new InMemoryFilmStorage(), storage);
    }

    @Test
    public void shouldWeAddLike() throws NotFoundException, DuplicatedDataException, CorruptedDataException {
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

        storage.addUser(user);
        service.addFilm(film);
        service.addLike(user.getId(), film.getId());

        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));
    }

    @Test
    public void shouldWeDoNotAddTwoLikeByOneUser() throws NotFoundException, DuplicatedDataException, CorruptedDataException {
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

        storage.addUser(user);
        service.addFilm(film);
        service.addLike(user.getId(), film.getId());
        service.addLike(user.getId(), film.getId());

        Assertions.assertEquals(film.getLikedUsers().size(), 1);
    }

    @Test
    public void shouldWeUpdateFilm() throws CorruptedDataException, NotFoundException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        service.addFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        service.updateFilm(newFilm2);
        Film updateFilm = service.getFilm(newFilm2.getId());

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
        service.addFilm(newFilm);

        Film newFilm2 = Film.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(CorruptedDataException.class, () -> service.updateFilm(newFilm2));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateFilmWithNewId() throws CorruptedDataException {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        service.addFilm(newFilm);
        Film newFilm2 = Film.builder()
                .id(newFilm.getId() + 1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> service.updateFilm(newFilm2));
    }

    @Test
    public void shouldWeDeleteLike() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
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

        storage.addUser(user);
        service.addFilm(film);
        service.addLike(user.getId(), film.getId());
        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));

        service.deleteLike(user.getId(), film.getId());
        Assertions.assertFalse(film.getLikedUsers().contains(user.getId()));
    }
}
