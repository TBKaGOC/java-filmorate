package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
@Slf4j
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {
    //Не проходило по длине checkStyle
    private static final String FIND_ALL =
            "SELECT * " +
                    "FROM films";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * " +
                    "FROM films " +
                    "WHERE id = ?";
    private static final String FIND_MOST_POPULAR_QUERY =
            "SELECT id, name, description, release_date, duration, rating_id " +
                    "FROM films AS f " +
                    "    LEFT OUTER JOIN liked_user AS l ON f.id = l.film_id " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) " +
                    "DESC LIMIT ?";
    private static final String ADD_QUERY =
            "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String ADD_GENRE_QUERY =
            "INSERT INTO film_genre (film_id, genre_id) " +
                    "VALUES (?, ?)";
    private static final String ADD_LIKE_QUERY =
            "INSERT INTO liked_user (film_id, user_id) " +
                    "VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                    "WHERE id = ?";
    private static final String DELETE_QUERY =
            "DELETE FROM films WHERE id = ?";
    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM liked_user " +
                    "WHERE film_id = ? AND user_id = ?";
    private static final String CONTAINS_QUERY =
            "SELECT EXISTS(SELECT id FROM films WHERE id = ?) AS b";
    private static final String FIND_DIRECTOR_FILMS_ORDER_YEAR_QUERY = "SELECT f.* FROM films_directors AS fd " +
            "LEFT JOIN films AS f ON fd.film_id = f.id " +
            "WHERE fd.director_id = ? " +
            "ORDER BY EXTRACT(YEAR FROM f.release_date)";
    private static final String FIND_DIRECTOR_FILMS_ORDER_LIKES_QUERY = "SELECT f.id, f.name, f.description, " +
            "f.release_date, f.duration, f.rating_id " +
            "FROM films_directors AS fd " +
            "LEFT JOIN films AS f ON fd.film_id = f.id " +
            "LEFT JOIN liked_user AS l ON l.film_id = f.id " +
            "WHERE fd.director_id = ? " +
            "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
            "ORDER BY COUNT(l.user_id) DESC";
    private static final String FIND_DIRECTOR_FILMS_QUERY = "SELECT f.* FROM films_directors AS fd " +
            "LEFT JOIN films AS f ON fd.film_id = f.id " +
            "WHERE fd.director_id = ? ";
    private static final String FIND_LIKES_BY_ID_QUERY = "SELECT user_id FROM liked_user WHERE film_id = ?";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO films_directors(film_id, director_id)" +
            "VALUES (?, ?)";
    private static final String DELETE_FROM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String DELETE_FROM_LIKED_USER_QUERY = "DELETE FROM liked_user WHERE film_id = ?";
    private static final String DELETE_FROM_FILMS_DIRECTORS_QUERY = "DELETE FROM films_directors WHERE film_id = ?";
    private static final String DELETE_FROM_REVIEWS_QUERY = "DELETE FROM reviews WHERE film_id = ?";
    private static final String GET_USERS_FILMS_QUERY = "SELECT id, name, description, release_date, duration, rating_id FROM films AS f JOIN liked_user AS lu ON lu.film_id = f.id WHERE lu.user_id = ?";

    private final RatingDbStorage ratingStorage;
    private final GenreDbStorage genreStorage;
    private static final String FIND_COMMONFILMS =
            "SELECT id, name, description, release_date, duration, rating_id " +
            "FROM( " +
            "     SELECT film_id " +
            "     FROM liked_user " +
            "     WHERE user_id = ? " +
            "     INTERSECT " +
            "     SELECT film_id " +
            "     FROM liked_user " +
            "     WHERE user_id = ? " +
            "    ) l " +
            "   JOIN films f on f.id = l.film_id " +
            "   JOIN liked_user ls on ls.film_id = f.id " +
            "GROUP BY id, name, description, release_date, duration, rating_id " +
            "ORDER BY COUNT(ls.user_id)";
    private final ReviewDbStorage reviewDbStorage;
    private final DirectorDbStorage directorDbStorage;


    public FilmDbStorage(JdbcTemplate jdbc,
                         RowMapper<Film> mapper,
                         RatingDbStorage ratingStorage,
                         GenreDbStorage genreStorage,
                         ReviewDbStorage reviewDbStorage, DirectorDbStorage directorDbStorage) {
        super(jdbc, mapper);
        this.ratingStorage = ratingStorage;
        this.genreStorage = genreStorage;
        this.reviewDbStorage = reviewDbStorage;
        this.directorDbStorage = directorDbStorage;
    }

    @Override
    public Collection<Film> getFilms() throws NotFoundException {
        Collection<Film> films = new ArrayList<>();
        for (Film e : findMany(FIND_ALL)) {
            foldFilm(e.getId(), e);
            films.add(e);
        }
        return films;
    }

    @Override
    public Film getFilm(Integer id) throws NotFoundException {
        try {
            Film film = findOne(FIND_BY_ID_QUERY, id)
                    .orElseThrow(() -> new NotFoundException("Не найден фильм " + id));
            foldFilm(id, film);

            return film;
        } catch (NotFoundException e) {
            log.warn("Не удалось получить фильм {}", id);
            throw e;
        }
    }

    @Override
    public List<Film> getMostPopular(int count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            return findAllByGenreAndYear(genreId, year).stream().limit(count).toList();
        } else if (genreId != null) {
            return findAllByGenre(genreId).stream().limit(count).toList();
        } else if (year != null) {
            return findAllByYear(year).stream().limit(count).toList();
        }
        return findMany(FIND_MOST_POPULAR_QUERY, count);
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
            StringBuilder addGenreQuery = new StringBuilder();
            StringBuilder containsGenreQuery = new StringBuilder();
            for (Genre genre : film.getGenres()) {
                containsGenreQuery.append("SELECT EXISTS(SELECT genre_id FROM genre WHERE genre_id = ")
                        .append(genre.getId())
                        .append(") AS b;");
                addGenreQuery.append("INSERT INTO film_genre (film_id, genre_id) VALUES (")
                        .append(film.getId())
                        .append(", ")
                        .append(genre.getId())
                        .append(");");
            }
            List<Boolean> contained = jdbc.queryForList(containsGenreQuery.toString(), Boolean.class);
            if (contained.contains(false)) {
                log.warn("Не удалось добавить фильм {}", film.getId());
                throw new CorruptedDataException("Жанры не найден");
            }
            update(addGenreQuery.toString());
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
                film.getRating().getId(),
                film.getId());
    }

    @Override
    public void deleteFilm(Integer id) {
        delete(DELETE_FROM_REVIEWS_QUERY, id);
        delete(DELETE_FROM_FILMS_DIRECTORS_QUERY, id);
        delete(DELETE_FROM_LIKED_USER_QUERY, id);
        delete(DELETE_FROM_GENRE_QUERY, id);
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
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }

    @Override
    public Review getReview(int reviewId) throws NotFoundException {
        return reviewDbStorage.getReview(reviewId);
    }

    @Override
    public List<Review> getMostPopularReviews(int filmId, int count) {
        return reviewDbStorage.getMostPopularReviews(filmId, count);
    }

    @Override
    public int addReview(Review review) {
        return reviewDbStorage.addReview(review);
    }

    @Override
    public boolean containsReview(int id) {
        return reviewDbStorage.contains(id);
    }

    @Override
    public void updateReview(Review review) {
        reviewDbStorage.updateReview(review);
    }

    @Override
    public void deleteReview(int id) {
        reviewDbStorage.deleteReview(id);
    }

    @Override
    public void addReviewLike(int reviewId, int userid, int useful) {
        reviewDbStorage.addReviewLike(reviewId, userid, useful);
    }

    @Override
    public boolean containsReviewLike(int reviewId, int userid) {
        return reviewDbStorage.containsReviewLike(reviewId, userid);
    }

    @Override
    public void deleteReviewLike(int reviewId, int userid) {
        reviewDbStorage.deleteReviewLike(reviewId, userid);
    }

    @Override
    public List<Review> getReviews() {
        return reviewDbStorage.getReviews();
    }

    @Override
    public void updateReviewLike(int reviewId, int userid, int useful) {
        reviewDbStorage.updateReviewLike(reviewId, userid, useful);
    }

    @Override
    public List<Film> findDirectorFilmsOrderYear(int directorId) {
        return findMany(FIND_DIRECTOR_FILMS_ORDER_YEAR_QUERY, directorId);
    }

    @Override
    public List<Film> findDirectorFilmsOrderLikes(int directorId) {
        return findMany(FIND_DIRECTOR_FILMS_ORDER_LIKES_QUERY, directorId);
    }

    @Override
    public List<Film> findDirectorFilms(int directorId) {
        return findMany(FIND_DIRECTOR_FILMS_QUERY, directorId);
    }

    @Override
    public LinkedHashSet<Integer> getLikes(int filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_LIKES_BY_ID_QUERY,
                (rs, rowNum) -> rs.getInt("user_id"), filmId));
    }

    @Override
    public void addDirectorId(int filmId, int directorId) throws DuplicatedDataException {
        try {
            update(INSERT_FILM_DIRECTOR_QUERY, filmId, directorId);
        } catch (SQLWarningException e) {
            throw new DuplicatedDataException(String.format("Для фильма %s режиссер %s уже установлен. %s",
                    filmId, directorId, e.getSQLWarning()));
        }
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return findMany(FIND_COMMONFILMS, userId, friendId)
                .stream()
                .peek(i -> {
                    try {
                        foldFilm(i.getId(), i);
                    } catch (NotFoundException ignored) {

                    }
                })
                .toList();
    }

    private void foldFilm(Integer id, Film result) throws NotFoundException {
        List<Integer> likes = jdbc.queryForList("SELECT user_id FROM liked_user WHERE film_id = ?",
                Integer.class, id);
        List<Integer> genres = jdbc.queryForList(
                "SELECT genre_id FROM film_genre WHERE film_id = ?",
                Integer.class, id);
        Set<Genre> resultGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

        for (Integer genre : genres) {
            resultGenres.add(genreStorage.getGenre(genre));
        }
        List<Integer> directorsIds = jdbc.queryForList(
                "SELECT director_id FROM films_directors WHERE film_id = ?",
                Integer.class, id);
        LinkedHashSet<Director> directors = new LinkedHashSet<>();
        for (Integer directorId : directorsIds) {
            directors.add(directorDbStorage.findDirector(directorId));
        }
        result.setGenres(resultGenres);
        result.setLikedUsers(Set.copyOf(likes));
        result.setDirectors(directors);
    }

    private List<Film> findAllByGenre(int genreId) {
        return findMany("SELECT id, name, description, release_date, duration, rating_id FROM films AS f LEFT OUTER JOIN liked_user AS l ON f.id = l.film_id WHERE f.id IN (SELECT film_id FROM film_genre WHERE genre_id = ?) GROUP BY f.id ORDER BY COUNT(l.user_id) DESC", genreId);
    }

    private List<Film> findAllByYear(int year) {
        return findMany("SELECT id, name, description, release_date, duration, rating_id FROM films AS f LEFT OUTER JOIN liked_user AS l ON f.id = l.film_id WHERE EXTRACT(YEAR FROM f.release_date) = ? GROUP BY f.id ORDER BY COUNT(l.user_id) DESC", year);
    }

    private List<Film> findAllByGenreAndYear(int genreId, int year) {
        return findMany("SELECT id, name, description, release_date, duration, rating_id FROM films AS f LEFT OUTER JOIN liked_user AS l ON f.id = l.film_id WHERE f.id IN (SELECT film_id FROM film_genre WHERE genre_id = ?) AND EXTRACT(YEAR FROM f.release_date) = ? GROUP BY f.id ORDER BY COUNT(l.user_id) DESC", genreId, year);
    }

    @Override
    public Collection<Film> getUsersLikedFilms(int userId) {
        return findMany(GET_USERS_FILMS_QUERY, userId);
    }
}
