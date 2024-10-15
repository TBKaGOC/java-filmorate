package ru.yandex.practicum.filmorate.storage;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage storage;
    private final UserDbStorage userStorage;

    @Test
    public void testGetAllFilms() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        Film film1 = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        storage.addFilm(film);
        storage.addFilm(film1);
        Collection<Film> films = storage.getFilms();

        Assertions.assertFalse(films.isEmpty());
    }

    @Test
    public void testGetFilm() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        storage.addFilm(film);
        Film film2 = storage.getFilm(film.getId());

        Assertions.assertNotNull(film2);
        Assertions.assertEquals(film2.getId(), film.getId());
    }

    @Test
    public void testGetMostPopular() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        storage.addFilm(film);
        List<Film> films = storage.getMostPopular(2, null, null);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(film.getId())));
    }

    @Test
    public void testGetMostPopularWithGenre() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(Set.of(Genre.builder().id(6).build()))
                .build();
        storage.addFilm(film);
        List<Film> films = storage.getMostPopular(2, 6, null);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(film.getId())));
    }

    @Test
    public void testGetMostPopularWithYear() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.of(2010, 3, 3))
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        storage.addFilm(film);
        List<Film> films = storage.getMostPopular(2, null, 2010);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(film.getId())));
    }

    @Test
    public void testGetMostPopularWithGenreAndYear() throws NotFoundException, CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.of(1997, 3, 3))
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(Set.of(Genre.builder().id(5).build()))
                .build();
        storage.addFilm(film);
        List<Film> films = storage.getMostPopular(1, 5, 1997);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(film.getId())));
    }

    @Test
    public void testAddFilm() throws CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(Set.of(Genre.builder().id(1).build(), Genre.builder().id(3).build()))
                .build();

        storage.addFilm(film);

        Assertions.assertTrue(storage.contains(film.getId()));
    }

    @Test
    public void testAddLike() throws CorruptedDataException, NotFoundException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();

        userStorage.addUser(user);
        storage.addFilm(film);
        storage.addLike(user.getId(), film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));
    }

    @Test
    public void testUpdateFilm() throws CorruptedDataException, NotFoundException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();

        storage.addFilm(film);

        Film film2 = Film.builder()
                .id(film.getId())
                .name("nameNew")
                .description("descNew")
                .duration(1233)
                .releaseDate(LocalDate.now().minusDays(12))
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        storage.updateFilm(film2);

        Film newFilm = storage.getFilm(film.getId());

        Assertions.assertEquals(film2.getName(), newFilm.getName());
        Assertions.assertEquals(film2.getDescription(), newFilm.getDescription());
        Assertions.assertEquals(film2.getDuration(), newFilm.getDuration());
        Assertions.assertEquals(film2.getReleaseDate(), newFilm.getReleaseDate());
    }

    @Test
    public void testDeleteFilm() throws CorruptedDataException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparing(Genre::getId)))
                .build();

        storage.addFilm(film);

        Assertions.assertTrue(storage.contains(film.getId()));

        storage.deleteFilm(film.getId());

        Assertions.assertFalse(storage.contains(film.getId()));
    }

    @Test
    public void testDeleteLike() throws CorruptedDataException, NotFoundException {
        Film film = Film.builder()
                .name("name")
                .description("desc")
                .duration(123)
                .releaseDate(LocalDate.now())
                .likedUsers(new HashSet<>())
                .rating(Rating.builder().id(3).name("PG-13").build())
                .genres(new TreeSet<>(Comparator.comparingInt(Genre::getId)))
                .build();
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();

        userStorage.addUser(user);
        storage.addFilm(film);
        storage.addLike(user.getId(), film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertTrue(film.getLikedUsers().contains(user.getId()));

        storage.deleteLike(user.getId(), film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertFalse(film.getLikedUsers().contains(user.getId()));
    }

    @Test
    public void shouldWeGetUsersLikedFilms() throws NotFoundException, CorruptedDataException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        newUser.setId(userStorage.addUser(newUser));

        Film film = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .rating(Rating.builder().id(3).name("PG-13").build())
                .build();
        film.setId(storage.addFilm(film));
        storage.addLike(newUser.getId(), film.getId());

        Assertions.assertEquals(storage.getUsersLikedFilms(newUser.getId()), List.of(film));
    }

    @Test
    public void shouldWeGetUsersLikedFilmsWithoutLikes() throws NotFoundException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userStorage.addUser(newUser);
        Assertions.assertEquals(storage.getUsersLikedFilms(newUser.getId()), List.of());
    }
}
