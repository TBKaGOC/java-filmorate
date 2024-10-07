package ru.yandex.practicum.filmorate.storage.dao.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.handler.ErrorHandler;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.BaseDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final RatingDbStorage storage;
    private final ErrorHandler errorHandler;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            return Film.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(LocalDate.parse(rs.getString("release_date"),
                            BaseDbStorage.formatterForBdDate))
                    .duration(rs.getInt("duration"))
                    .rating(storage.getRating(rs.getInt("rating_id")))
                    .build();
        } catch (NotFoundException e) {
            log.warn("Ошибка преобразования фильма");
            errorHandler.notFound(e);
            return null;
        }
    }
}
