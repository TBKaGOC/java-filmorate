package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

public class FilmServiceTests {
    private FilmService service;
    private UserStorage storage;

    @BeforeEach
    public void createNewService() {
        storage = new InMemoryUserStorage();
        service = new FilmService(new InMemoryFilmStorage(), storage, new FilmMapper(),
                null,null,null);
    }

    @Test
    public void shouldWeAddLike() throws NotFoundException, DuplicatedDataException, CorruptedDataException {
        User user = User.builder()
                .id(1)
                .name("user")
                .login("user")
                .email("email@email.e")
                .build();
        FilmDto film = FilmDto.builder()
                .id(1)
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();

        storage.addUser(user);
        service.addFilm(film);
        service.addLike(user.getId(), film.getId());
        film = service.getFilm(film.getId());

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
        FilmDto film = FilmDto.builder()
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
        film = service.getFilm(film.getId());

        Assertions.assertEquals(film.getLikedUsers().size(), 1);
    }

    @Test
    public void shouldWeUpdateFilm() throws CorruptedDataException, NotFoundException, DuplicatedDataException {
        FilmDto newFilm = FilmDto.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        service.addFilm(newFilm);
        FilmDto newFilm2 = FilmDto.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        service.updateFilm(newFilm2);
        FilmDto updateFilm = service.getFilm(newFilm.getId());

        Assertions.assertEquals(newFilm2.getId(), updateFilm.getId());
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateLocalDateIsBeforeEarlyDate() throws CorruptedDataException, NotFoundException, DuplicatedDataException {
        FilmDto newFilm = FilmDto.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();
        service.addFilm(newFilm);

        FilmDto newFilm2 = FilmDto.builder()
                .id(newFilm.getId())
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrows(CorruptedDataException.class, () -> service.updateFilm(newFilm2));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateFilmWithNewId() throws CorruptedDataException, NotFoundException, DuplicatedDataException {
        FilmDto newFilm = FilmDto.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(120)
                .build();

        service.addFilm(newFilm);
        FilmDto newFilm2 = FilmDto.builder()
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
        FilmDto film = FilmDto.builder()
                .id(1)
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();

        storage.addUser(user);
        service.addFilm(film);
        service.addLike(user.getId(), film.getId());
        film = service.getFilm(film.getId());
        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));

        service.deleteLike(user.getId(), film.getId());
        Assertions.assertFalse(film.getLikedUsers().contains(user.getId()));
    }
}
