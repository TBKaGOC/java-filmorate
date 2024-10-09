package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Collection<Film> getFilms() throws NotFoundException;

    Film getFilm(Integer id) throws NotFoundException;

    List<Film> getMostPopular(String count);

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
}
