package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;

@Component
@Slf4j
public class RatingDbStorage extends BaseDbStorage<Rating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE rating_id = ?";
    private static final String CONTAINS_QUERY = "SELECT EXISTS(SELECT rating_id FROM rating WHERE rating_id = ?) AS b";
    private static final String FIND_RATING_ID_QUERY="SELECT rating_id FROM films AS f WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Rating> getRatings() {
        return findMany(FIND_ALL_QUERY);
    }

    public Rating getRating(Integer id) throws NotFoundException {
        try {
            return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Не найден рейтинг " + id));
        } catch (NotFoundException e) {
            log.warn("Не удалось получить рейтинг {}", id);
            throw e;
        }
    }

    public boolean contains(Integer id) {
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }

    public Integer findRatingIdByFilmId(int filmId) {
        return jdbc.queryForObject(FIND_RATING_ID_QUERY, Integer.class, filmId);
    }
}
