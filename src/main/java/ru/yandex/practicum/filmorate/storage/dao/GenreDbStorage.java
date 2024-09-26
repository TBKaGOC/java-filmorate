package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Component
@Slf4j
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(Integer id) throws NotFoundException {
        Optional<Genre> genre = findOne(FIND_BY_ID_QUERY, id);

        if (genre.isPresent()) {
            return genre.get();
        } else {
            log.warn("Не удалось получить жанр {}", id);
            throw new NotFoundException("Жанр " + id + " не найден");
        }
    }

    public boolean contains(Integer id) {
        try {
            getGenre(id);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
