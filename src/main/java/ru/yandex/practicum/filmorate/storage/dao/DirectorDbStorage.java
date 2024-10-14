package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.LinkedHashSet;

@Component
@Slf4j
@Primary
public class DirectorDbStorage extends BaseDbStorage<Director> {

    private static final String FIND_ALL = "SELECT * FROM directors";
    private static final String FIND_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String FIND_BY_NAME = "SELECT * FROM directors WHERE name = ?";
    private static final String INSERT_DIRECTOR = "INSERT INTO directors(name)VALUES (?)";
    private static final String UPDATE = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM directors WHERE id = ?";
    private static final String FIND_DIRECTOR_ID_QUERY = "SELECT director_id FROM films_directors WHERE film_id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public boolean isDirectorWithSameNameExist(String name) {
        return findOne(FIND_BY_NAME, name).isPresent();
    }

    public Director findDirector(Director director) throws NotFoundException {
        return findOne(FIND_BY_ID, director.getId())
                .orElseThrow(() -> new NotFoundException(String.format("В запросе не корректный режиссер с ID %d", director.getId())));
    }

    public Director findDirector(int directorId) throws NotFoundException {
        return findOne(FIND_BY_ID, directorId)
                .orElseThrow(() -> new NotFoundException(String.format("Режиссер с ID %d не найден", directorId)));
    }

    public Collection<Director> findAll() {
        return findMany(FIND_ALL);
    }

    public Director create(Director director) {
        int id = insert(INSERT_DIRECTOR, director.getName());
        director.setId(id);
        return director;
    }

    public Director update(Director newDirector) {
        update(UPDATE, newDirector.getName(), newDirector.getId());
        return newDirector;
    }

    public boolean delete(int directorId) {
        return delete(DELETE, directorId);
    }

    public LinkedHashSet<Integer> findDirectorsIdsByFilmId(int filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_DIRECTOR_ID_QUERY,
                (rs, rowNum) -> rs.getInt("director_id"), filmId));
    }

}
