package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class RatingDbStorage extends BaseDbStorage<Rating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE rating_id = ?";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Rating> getRatings() {
        return findMany(FIND_ALL_QUERY);
    }

    public Rating getRating(Integer id) throws NotFoundException {
        Optional<Rating> rating = findOne(FIND_BY_ID_QUERY, id);

        if (rating.isPresent()) {
            return rating.get();
        } else {
            log.warn("Не удалось получить рейтинг {}", id);
            throw new NotFoundException("Рейтинг " + id + " не найден");
        }
    }

    public boolean contains(Integer id) {
        return findOne(FIND_BY_ID_QUERY, id).isPresent();
    }
}
