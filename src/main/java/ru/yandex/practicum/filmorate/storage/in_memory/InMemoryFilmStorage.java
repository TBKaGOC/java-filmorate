package ru.yandex.practicum.filmorate.storage.in_memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
@Slf4j
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Review> reviews = new HashMap<>();
    private final Map<Integer, HashMap<Integer, Integer>> reviewsLikes = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getFilm(Integer id) throws NotFoundException {
        if (!films.containsKey(id)) {
            log.warn("Не удалось получить фильм {}", id);
            throw new NotFoundException("Фильм " + id + " не найден");
        }

        return films.get(id);
    }

    @Override
    public List<Film> getMostPopular(int count, Integer genreId, Integer year) {
        List<Film> resultList = films.values().stream().toList();
        if (genreId != null && year != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        } else if (genreId != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
                    .toList();
        } else if (year != null) {
            resultList = resultList.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        }
        return resultList.stream()
                .sorted(Comparator.comparing(Film::getLikesNumber).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Integer addFilm(Film film) throws CorruptedDataException {
        if (film.getReleaseDate().isBefore(Film.EARLY_DATE)) {
            log.warn("Не удалось добавить новый фильм");
            throw new CorruptedDataException("Фильм не может выйти раньше 28 декабря 1895 года");
        }
        film.setId(getNextId(films));

        films.put(film.getId(), film);
        return film.getId();
    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
    }

    @Override
    public void addLike(int likedUser, int film) throws NotFoundException {
        getFilm(film).addLike(likedUser);
    }

    @Override
    public void deleteLike(int unlikedUser, int film) throws NotFoundException {
        getFilm(film).deleteLike(unlikedUser);
    }

    @Override
    public boolean contains(Integer id) {
        return films.containsKey(id);
    }

    @Override
    public Review getReview(int reviewId) throws NotFoundException {
        if (!reviews.containsKey(reviewId)) {
            log.warn("Не удалось получить отзыв {}", reviewId);
            throw new NotFoundException("Отзыв " + reviewId + " не найден");
        }

        return reviews.get(reviewId);
    }

    @Override
    public List<Review> getMostPopularReviews(int filmId, int count) {
        return reviews.values()
                .stream()
                .filter(i -> i.getFilmId() == filmId)
                .sorted(Comparator.comparingInt(i -> -1 * i.getUseful()))
                .limit(count)
                .toList();
    }

    @Override
    public int addReview(Review review) {
        var id = getNextId(reviews);
        reviews.put(id, review);

        return id;
    }

    @Override
    public boolean containsReview(int id) {
        return reviews.containsKey(id);
    }

    @Override
    public void updateReview(Review review) {
        reviews.replace(review.getReviewId(), review);
    }

    @Override
    public void deleteReview(int id) {
        reviews.remove(id);
        reviewsLikes.remove(id);
    }

    @Override
    public void addReviewLike(int reviewId, int userid, int useful) {
        if (!reviews.containsKey(reviewId))
            return;

        var review = reviews.get(reviewId);

        if (!reviewsLikes.containsKey(reviewId)) {
            var likes = new HashMap<>();
            likes.put(userid, useful);
        } else {
            var likes = reviewsLikes.get(reviewId);

            if (likes.containsKey(useful))
                return;

            likes.put(userid, useful);
        }

        reviews.replace(
                reviewId,
                review.toBuilder()
                        .useful(review.getUseful() + useful)
                        .build());
    }

    @Override
    public boolean containsReviewLike(int reviewId, int userid) {
        if (!reviewsLikes.containsKey(reviewId))
            return false;

        return reviewsLikes.get(reviewId).containsKey(userid);
    }

    @Override
    public void deleteReviewLike(int reviewId, int userid) {
        if (!reviews.containsKey(reviewId))
            return;

        var review = reviews.get(reviewId);

        if (!reviewsLikes.containsKey(reviewId))
            return;

        var likes = reviewsLikes.get(reviewId);

        if (!likes.containsKey(userid))
            return;

        var useful = likes.get(userid);

        likes.remove(userid);

        reviews.replace(
                reviewId,
                review.toBuilder()
                        .useful(review.getUseful() - useful)
                        .build());
    }

    @Override
    public List<Review> getReviews() {
        return reviews.values().stream().toList();
    }

    @Override
    public void updateReviewLike(int reviewId, int userid, int useful) {
        if (!reviews.containsKey(reviewId))
            return;

        var review = reviews.get(reviewId);

        if (!reviewsLikes.containsKey(reviewId))
            return;

        var likes = reviewsLikes.get(reviewId);

        if (!likes.containsKey(userid))
            return;

        likes.replace(userid, useful);

        reviews.replace(
                reviewId,
                review.toBuilder()
                        .useful(review.getUseful() - useful)
                        .build());
    }

    @Override
    public List<Film> findDirectorFilmsOrderYear(int directorId) {
        return null;
    }

    @Override
    public List<Film> findDirectorFilmsOrderLikes(int directorId) {
        return null;
    }

    @Override
    public List<Film> findDirectorFilms(int directorId) {
        return null;
    }

    @Override
    public LinkedHashSet<Integer> getLikes(int filmId) {
        return null;
    }

    @Override
    public void addDirectorId(int filmId, int directorId) throws DuplicatedDataException {

    }

    @Override
    public Collection<Film> searchByTitle(String query) {
        return null;
    }

    @Override
    public Collection<Film> searchByDirector(String query) {
        return null;
    }

    private <T> int getNextId(Map<Integer, T> map) {
        int currentMaxId = map.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return films
                .values()
                .stream()
                .filter(i -> i.getLikedUsers().contains(userId) && i.getLikedUsers().contains(friendId))
                .sorted(Comparator.comparingInt(i -> -1 * i.getLikesNumber()))
                .toList();
    }
}
