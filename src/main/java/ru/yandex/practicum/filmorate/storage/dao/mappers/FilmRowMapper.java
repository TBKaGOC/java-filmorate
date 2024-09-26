package ru.yandex.practicum.filmorate.storage.dao.mappers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final RatingDbStorage storage;

    @SneakyThrows
    @Override
    public Film mapRow(ResultSet rs, int rowNum) {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("release_date"), formatter))
                .duration(rs.getInt("duration"))
                .rating(storage.getRating(rs.getInt("rating_id")))
                .build();
    }
}
