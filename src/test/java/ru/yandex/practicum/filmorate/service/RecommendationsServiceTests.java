package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RecommendationsServiceTests {
    private RecommendationsService recommendationsService;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private FilmMapper filmMapper;

    @BeforeEach
    public void createNewService() {
        filmMapper = new FilmMapper();
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        recommendationsService = new RecommendationsService(filmStorage, userStorage, new FilmMapper());
    }

    @Test
    public void shouldWeGetCorrectRecommendations() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        User firstUser = User.builder()
                .login("firstLogin")
                .name("firstName")
                .email("rightemail1@email.right")
                .birthday(LocalDate.of(2000, 2, 1))
                .build();
        userStorage.addUser(firstUser);

        User secondUser = User.builder()
                .login("secondLogin")
                .name("secondName")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2003, 5, 1))
                .build();
        userStorage.addUser(secondUser);

        Film firstFilm = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("firstDescription")
                .name("firstFilm")
                .build();
        filmStorage.addFilm(firstFilm);
        filmStorage.addLike(firstUser.getId(), firstFilm.getId());
        filmStorage.addLike(secondUser.getId(), firstFilm.getId());

        Film secondFilm = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("secondDescription")
                .name("secondFilm")
                .likedUsers(new HashSet<>(firstUser.getId()))
                .build();
        filmStorage.addFilm(secondFilm);
        filmStorage.addLike(firstUser.getId(), secondFilm.getId());

        Assertions.assertEquals(recommendationsService.getRecommendations(secondUser.getId()), Set.of(secondFilm).stream().map(film -> filmMapper.mapToFilmDto(film)).collect(Collectors.toSet()));
    }

    @Test
    public void shouldWeGetEmptyCollectionIfUserNotLikedAnyFilm() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        User firstUser = User.builder()
                .login("firstLogin")
                .name("firstName")
                .email("rightemail1@email.right")
                .birthday(LocalDate.of(2000, 2, 1))
                .build();
        userStorage.addUser(firstUser);

        User secondUser = User.builder()
                .login("secondLogin")
                .name("secondName")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2003, 5, 1))
                .build();
        userStorage.addUser(secondUser);

        Film firstFilm = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("firstDescription")
                .name("firstFilm")
                .build();
        filmStorage.addFilm(firstFilm);
        filmStorage.addLike(firstUser.getId(), firstFilm.getId());

        Assertions.assertEquals(recommendationsService.getRecommendations(secondUser.getId()), Set.of());
    }

    @Test
    public void shouldWeGetEmptyCollectionIfUsersHaveSameLikes() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        User firstUser = User.builder()
                .login("firstLogin")
                .name("firstName")
                .email("rightemail1@email.right")
                .birthday(LocalDate.of(2000, 2, 1))
                .build();
        userStorage.addUser(firstUser);

        User secondUser = User.builder()
                .login("secondLogin")
                .name("secondName")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2003, 5, 1))
                .build();
        userStorage.addUser(secondUser);

        Film firstFilm = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("firstDescription")
                .name("firstFilm")
                .build();
        filmStorage.addFilm(firstFilm);
        filmStorage.addLike(firstUser.getId(), firstFilm.getId());
        filmStorage.addLike(secondUser.getId(), firstFilm.getId());

        Assertions.assertEquals(recommendationsService.getRecommendations(secondUser.getId()), Set.of());
    }
}