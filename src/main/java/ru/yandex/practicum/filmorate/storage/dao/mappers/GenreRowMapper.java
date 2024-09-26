package ru.yandex.practicum.filmorate.storage.dao.mappers;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;

@Component
public class GenreRowMapper implements RowMapper<Genre> {
    @SneakyThrows
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
