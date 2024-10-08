package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@Slf4j
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String CONTAINS_QUERY = "SELECT EXISTS(SELECT genre_id FROM genre WHERE genre_id = ?) AS b";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(Integer id) throws NotFoundException {
        try {
            return findOne(FIND_BY_ID_QUERY, id).orElseThrow(() -> new NotFoundException("Не найден жанр " + id));
        } catch (NotFoundException e) {
            log.warn("Не удалось получить жанр {}", id);
            throw e;
        }
    }

    public boolean contains(Integer id) {
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }
}
