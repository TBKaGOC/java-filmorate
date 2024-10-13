package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms() throws NotFoundException;

    Film getFilm(Integer id) throws NotFoundException;

    List<Film> getMostPopular(int count, Integer genreId, Integer year);

    Integer addFilm(Film film) throws CorruptedDataException, NotFoundException;

    void updateFilm(Film film);

    void deleteFilm(Integer id);

    void addLike(int likedUser, int film) throws NotFoundException;

    void deleteLike(int unlikedUser, int film) throws NotFoundException;

    boolean contains(Integer id);

    Review getReview(int reviewId) throws NotFoundException;

    List<Review> getMostPopularReviews(int filmId, int count);

    int addReview(Review review);

    boolean containsReview(int id);

    void updateReview(Review review);

    void deleteReview(int id);

    void addReviewLike(int reviewId, int userid, int useful);

    boolean containsReviewLike(int reviewId, int userid);

    void deleteReviewLike(int reviewId, int userid);

    List<Review> getReviews();

    void updateReviewLike(int reviewId, int userid, int useful);

    List<Film> findDirectorFilmsOrderYear(int directorId);

    List<Film> findDirectorFilmsOrderLikes(int directorId);

    List<Film> findDirectorFilms(int directorId);

    LinkedHashSet<Integer> getLikes(int filmId);

    void addDirectorId(int filmId, int directorId) throws DuplicatedDataException;


    Collection<Film> getCommonFilms(int userId, int friendId);

    Collection<Film> searchByTitle(String query);

    Collection<Film> searchByDirector(String query);
}
