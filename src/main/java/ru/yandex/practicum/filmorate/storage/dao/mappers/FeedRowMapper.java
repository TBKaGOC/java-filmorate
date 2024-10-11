package ru.yandex.practicum.filmorate.storage.dao.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class FeedRowMapper implements RowMapper<Feed> {
    @Override
    public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("eventType"))
                .operation(rs.getString("operation"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
