package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

public class FilmStorageTests {
    private FilmStorage filmStorage;
    private UserStorage userStorage;

//    @BeforeEach
//    public void createNewFilmController() {
//        filmStorage = new InMemoryFilmStorage();
//        userStorage = new InMemoryUserStorage();
//    }

    //@Test
    public void shouldWeGetAllFilms() throws CorruptedDataException, NotFoundException, DuplicatedDataException {
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

    //@Test
    public void shouldWeCreateNewFilm() throws CorruptedDataException, NotFoundException, DuplicatedDataException {
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

    //@Test
    public void shouldWeGetExceptionWhenLocalDateIsBeforeEarlyDate() {
        Film newFilm = Film.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1, 1, 1))
                .duration(120)
                .build();

        Assertions.assertThrowsExactly(CorruptedDataException.class, () -> filmStorage.addFilm(newFilm));
    }

    //@Test
    public void shouldWeGetMostPopularFilmsWithGenre() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        Set<Genre> genres = new HashSet<>();
        Genre genre = Genre
                .builder()
                .id(1)
                .name("Комедия")
                .build();
        genres.add(genre);

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
                    .genres(genres)
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

        Assertions.assertEquals(filmStorage.getMostPopular(5, genre.getId(), null), films);
    }

    //@Test
    public void shouldWeGetMostPopularFilmsWithYear() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
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

        Assertions.assertEquals(filmStorage.getMostPopular(5, null, LocalDate.now().getYear()), films);
    }

    //@Test
    public void shouldWeGetMostPopularFilmsWithGenreAndYear() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre
                .builder()
                .id(1)
                .name("Комедия")
                .build();
        genres.add(genre);

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
                    .genres(genres)
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

        Assertions.assertEquals(filmStorage.getMostPopular(5, genre.getId(), LocalDate.now().getYear()), films);
    }

    //@Test
    public void shouldWeGetMostPopularFilmsWithATopSizeLargerThanTheList() throws NotFoundException,
            CorruptedDataException, DuplicatedDataException {
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

        Assertions.assertEquals(filmStorage.getMostPopular(10, null, null), films);
    }

    //@Test
    public void shouldWeGetMostPopularFilmsWithATopSizeSmallerThanTheList() throws NotFoundException,
            CorruptedDataException, DuplicatedDataException {
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

        Assertions.assertEquals(filmStorage.getMostPopular(3, null, null), films);
    }

    //@Test
    public void shouldWeGetUsersLikedFilms() throws NotFoundException, CorruptedDataException, DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userStorage.addUser(newUser);

        Film film = Film.builder()
                .duration(1)
                .releaseDate(LocalDate.now())
                .description("description")
                .name("film")
                .build();
        filmStorage.addFilm(film);
        filmStorage.addLike(newUser.getId(), film.getId());

        Assertions.assertEquals(filmStorage.getUsersLikedFilms(newUser.getId()), List.of(film));
    }

    //@Test
    public void shouldWeGetUsersLikedFilmsWithoutLikes() throws NotFoundException, DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userStorage.addUser(newUser);
        Assertions.assertEquals(filmStorage.getUsersLikedFilms(newUser.getId()), List.of());
    }
}
