package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
@Slf4j
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    private static final String FIND_ALL = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String FIND_MOST_POPULAR_QUERY = "SELECT id, name, description, release_date, duration, rating_id FROM films AS f LEFT OUTER JOIN liked_user AS l ON f.id = l.film_id GROUP BY f.id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String ADD_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String ADD_LIKE_QUERY = "INSERT INTO liked_user (film_id, user_id) VALUES (?, ?)";

    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM liked_user WHERE film_id = ? AND user_id = ?";
    private final RatingDbStorage ratingStorage;
    private final GenreDbStorage genreStorage;


    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper, RatingDbStorage ratingStorage, GenreDbStorage genreStorage) {
        super(jdbc, mapper);
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
    }

    @Override
    public Collection<Film> getFilms() {
        return findMany(FIND_ALL);
    }

    @Override
    public Film getFilm(Integer id) throws NotFoundException {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);

        if (film.isPresent()) {
            Film result = film.get();
            List<Integer> likes = jdbc.queryForList("SELECT user_id FROM liked_user WHERE film_id = ?",
                    Integer.class, id);
            List<Integer> genres = jdbc.queryForList("SELECT genre_id FROM film_genre WHERE film_id = ?",
                    Integer.class, id);
            Set<Genre> resultGenres = new HashSet<>();

            for (Integer genre: genres) {
                resultGenres.add(genreStorage.getGenre(genre));
            }
            result.setGenres(resultGenres);
            result.setLikedUsers(Set.copyOf(likes));

            return result;
        } else {
            log.warn("Не удалось получить фильм {}", id);
            throw new NotFoundException("Фильм " + id + " не найден");
        }
    }

    @Override
    public List<Film> getMostPopular(String count) {
        return findMany(FIND_MOST_POPULAR_QUERY, Integer.parseInt(count));
    }

    @Override
    public Integer addFilm(Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("Не удалось добавить новый фильм");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }

        int ratingId = film.getRating().getId();

        if (!ratingStorage.contains(ratingId)) {
            log.warn("Не удалось добавить фильм {}", film.getId());
            throw new CorruptedDataException("Рейтинг " + ratingId + " не найден");
        }

        int id = (int) insert(ADD_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                ratingId);

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (!genreStorage.contains(genre.getId())) {
                    log.warn("Не удалось добавить фильм {}", film.getId());
                    throw new CorruptedDataException("Рейтинг " + genre.getId() + " не найден");
                }
                update(ADD_GENRE_QUERY, id, genre.getId());
            }
        }
        return id;
    }

    @Override
    public void updateFilm(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getName(),
                film.getId());
    }

    @Override
    public void deleteFilm(Integer id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void addLike(int likedUser, int film) {
        update(ADD_LIKE_QUERY, film, likedUser);
    }

    @Override
    public void deleteLike(int unlikedUser, int film) {
        update(DELETE_LIKE_QUERY, film, unlikedUser);
    }

    @Override
    public boolean contains(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id).isPresent();
    }
}
