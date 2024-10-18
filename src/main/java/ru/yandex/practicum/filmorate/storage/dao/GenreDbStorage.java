package ru.yandex.practicum.filmorate.storage.dao;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
@Slf4j
public class GenreDbStorage extends BaseDbStorage<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String CONTAINS_QUERY = "SELECT EXISTS(SELECT genre_id FROM genre WHERE genre_id = ?) AS b";
    private static final String FIND_GENRE_OBJECT_BY_FILM = "SELECT * FROM genre WHERE genre_id IN (" +
            "SELECT genre_id FROM film_genre WHERE film_id = ?" +
            ")";
    private static final String FIND_GENRE_ID_QUERY = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String DELETE_BY_FILMID_GENREID = "DELETE film_genre WHERE film_id = ? and genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Genre> getGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Collection<Genre> getGenreObjectByFilm(Integer film_id) {
        return findMany(FIND_GENRE_OBJECT_BY_FILM, film_id);
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

    public LinkedHashSet<Integer> findGenresIdsByFilmId(int filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_GENRE_ID_QUERY,
                (rs, rowNum) -> rs.getInt("genre_id"), filmId));
    }

    public void deleteGenreByFilmIdAndGenreId(int filmId, int genreId) {
        update(DELETE_BY_FILMID_GENREID, filmId, genreId);
    }
}
