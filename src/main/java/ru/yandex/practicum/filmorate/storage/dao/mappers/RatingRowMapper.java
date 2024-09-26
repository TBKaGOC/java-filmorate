package ru.yandex.practicum.filmorate.storage.dao.mappers;

import lombok.SneakyThrows;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;

@Component
public class RatingRowMapper implements RowMapper<Rating> {
    @SneakyThrows
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) {
        return Rating.builder()
                .id(rs.getInt("rating_id"))
                .name(rs.getString("name")).build();
    }
}
