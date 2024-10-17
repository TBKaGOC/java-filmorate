package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.mappers.ReviewRowMapper;

import java.util.List;

@Component
@Slf4j
public class ReviewDbStorage extends BaseDbStorage<Review> {
    private static final String FIND_ALL_QUERY =
            "SELECT r.review_id, r.content, r.isPositive, r.film_id, r.user_id, sum(l.useful) useful " +
                    "FROM reviews r " +
                    "   left join reviewLikes l on l.review_id = r.review_id " +
                    "GROUP BY r.review_id, r.content, r.isPositive, r.film_id, r.user_id " +
                    "ORDER BY useful desc";
    private static final String FIND_BY_ID_QUERY =
            "SELECT r.review_id, r.content, r.isPositive, r.film_id, r.user_id, sum(l.useful) useful " +
                    "FROM reviews r " +
                    "   left join reviewLikes l on l.review_id = r.review_id " +
                    "WHERE r.review_id = ? " +
                    "GROUP BY r.review_id, r.content, r.isPositive, r.film_id, r.user_id";
    private static final String CONTAINS_QUERY =
            "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id = ?) AS b";
    private static final String FIND_MOSTPOPULAR_BY_FILMID =
            "SELECT r.review_id, r.content, r.isPositive, r.film_id, r.user_id, sum(l.useful) useful " +
            "FROM reviews r " +
            "   left join reviewLikes l on l.review_id = r.review_id " +
            "WHERE r.film_id = ? " +
            "GROUP BY r.review_id, r.content, r.isPositive, r.film_id, r.user_id " +
            "ORDER BY CASE WHEN useful IS NULL THEN 0 ELSE USEFUL end desc " +
            "LIMIT ?";
    private static final String FIND_MOSTPOPULAR =
            "SELECT r.review_id, r.content, r.isPositive, r.film_id, r.user_id, sum(l.useful) useful " +
            "FROM reviews r " +
            "   left join reviewLikes l on l.review_id = r.review_id " +
            "GROUP BY r.review_id, r.content, r.isPositive, r.film_id, r.user_id " +
            "ORDER BY CASE WHEN useful IS NULL THEN 0 ELSE USEFUL end desc " +
            "LIMIT ?";
    private static final String ADD_QUERY =
            "INSERT INTO reviews (content, isPositive, film_id, user_id) " +
                    "values(?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE reviews set content = ?, isPositive = ?, film_id = ?, user_id = ? " +
                    "where review_id = ?";
    private static final String DELETE_REVIEW =
            "DELETE reviews " +
                    "where review_id = ?";
    private static final String DELETE_LIKE =
            "DELETE reviewLikes " +
                    "where review_id = ? and user_id = ?";
    private static final String ADD_LIKE =
            "INSERT INTO reviewLikes (review_id, user_id, useful) " +
                    "values(?, ?, ?)";
    private static final String UPDATE_LIKE =
            "UPDATE reviewLikes set useful = ? " +
                    "where review_id = ? and user_id = ?";
    private static final String CONTAINS_LIKE_QUERY =
            "SELECT EXISTS(SELECT 1 FROM reviewLikes WHERE review_id = ? and user_id = ?) AS b";
    private static final String DELETE_REVIEWS_BY_FILMID =
            "set @filmId = ? " +
                    "DELETE reviewLikes " +
                    "WHERE exists(select 1 " +
                    "             FROM reviews " +
                    "             WHERE film_id = @filmId) " +
                    "DELETE reviews " +
                    "WHERE film_id = @filmId";


    public ReviewDbStorage(JdbcTemplate jdbc, ReviewRowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Review> getReviews(int limit) {
        return findMany(FIND_MOSTPOPULAR, limit);
    }

    public Review getReview(Integer id) throws NotFoundException {
        try {
            return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Не найден отзыв " + id));
        } catch (NotFoundException e) {
            log.warn("Не удалось получить отзыв {}", id);
            throw e;
        }
    }

    public boolean contains(Integer id) {
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }

    public List<Review> getMostPopularReviews(int filmId, int count) {
        var result = findMany(FIND_MOSTPOPULAR_BY_FILMID, filmId, count);
        return result;
    }

    public int addReview(Review review) {
        return (int) insert(ADD_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getFilmId(),
                review.getUserId());
    }

    public void updateReview(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getFilmId(),
                review.getUserId(),
                review.getReviewId());
    }

    public void deleteReview(int id) {
        update(DELETE_REVIEW, id);
    }

    public boolean containsReviewLike(int reviewId, int userid) {
        return jdbc.queryForList(CONTAINS_LIKE_QUERY, Boolean.class, reviewId, userid)
                .getFirst();
    }

    public void addReviewLike(int reviewId, int userid, int useful) {
        update(ADD_LIKE, reviewId, userid, useful);
    }

    public void deleteReviewLike(int reviewId, int userid) {
        update(DELETE_LIKE, reviewId, userid);
    }

    public void deleteReviewsByFilmId(int filmId) {
        update(DELETE_REVIEWS_BY_FILMID, filmId);
    }

    public void updateReviewLike(int reviewId, int userid, int useful) {
        update(UPDATE_LIKE, useful, reviewId, userid);
    }
}
