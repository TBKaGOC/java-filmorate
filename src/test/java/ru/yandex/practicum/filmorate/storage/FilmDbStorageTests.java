package ru.yandex.practicum.filmorate.storage;

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
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {
    private final FilmDbStorage storage;

    @Test
    public void testGetAllFilms() throws NotFoundException {
        Collection<Film> films = storage.getFilms();

        Assertions.assertFalse(films.isEmpty());
    }

    @Test
    public void testGetFilm() throws NotFoundException {
        Film film = storage.getFilm(1);

        Assertions.assertNotNull(film);
        Assertions.assertEquals(film.getId(), 1);
    }

    @Test
    public void testGetMostPopular() throws NotFoundException {
        List<Film> films = storage.getMostPopular(2, null, null);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(2),
                storage.getFilm(4)));
    }

    @Test
    public void testGetMostPopularWithGenre() throws NotFoundException {
        List<Film> films = storage.getMostPopular(2, 6, null);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of( storage.getFilm(2),
                storage.getFilm(4)));
    }

    @Test
    public void testGetMostPopularWithYear() throws NotFoundException {
        List<Film> films = storage.getMostPopular(2, null, 2010);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(4)));
    }

    @Test
    public void testGetMostPopularWithGenreAndYear() throws NotFoundException {
        List<Film> films = storage.getMostPopular(1, 6, 1997);

        Assertions.assertNotNull(films);
        Assertions.assertEquals(films, List.of(storage.getFilm(2)));
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

        storage.addFilm(film);
        storage.addLike(1, film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertTrue(film.getLikedUsers().contains(1));
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

        Assertions.assertEquals(film.getName(), newFilm.getName());
        Assertions.assertEquals(film.getDescription(), newFilm.getDescription());
        Assertions.assertEquals(film.getDuration(), newFilm.getDuration());
        Assertions.assertEquals(film.getReleaseDate(), newFilm.getReleaseDate());
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

        storage.addFilm(film);
        storage.addLike(1, film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertTrue(film.getLikedUsers().contains(1));

        storage.deleteLike(1, film.getId());
        film = storage.getFilm(film.getId());

        Assertions.assertFalse(film.getLikedUsers().contains(1));
    }
}
