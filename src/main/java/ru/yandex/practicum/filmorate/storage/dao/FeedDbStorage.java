package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.dao.mappers.FeedRowMapper;

import java.util.Collection;

@Component
@Slf4j
public class FeedDbStorage extends BaseDbStorage<Feed> {
    private static final String FIND_ALL_QUERY =
            "SELECT * " +
            "FROM events";
    private static final String FIND_BY_ID_QUERY =
            "SELECT * " +
            "FROM events " +
            "WHERE event_id = ?";
    private static final String FIND_BY_USERID_QUERY =
            "SELECT * " +
            "FROM events " +
            "WHERE user_id = ? " +
            "ORDER BY timestamp";
    private static final String ADD_QUERY =
            "INSERT INTO events(timestamp, user_id, eventType, operation, entity_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_BY_ID =
            "DELETE events " +
            "WHERE event_id = ?";
    private static final String DELETE_BY_USERID =
            "DELETE events " +
            "WHERE user_id = ?";
    private static final String DELETE_BY_ENTITYID =
            "DELETE events " +
            "WHERE user_id = ? and entity_id = ?";

    public FeedDbStorage(JdbcTemplate jdbc, FeedRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Collection<Feed> getFeedByUserId(int userId) {
        return findMany(FIND_BY_USERID_QUERY, userId);
    }

    public void deleteFeedByUserId(int userId) {
        update(DELETE_BY_USERID, userId);

        log.trace(String.format("deleted events for userId=%d", userId));
    }

    public void deleteFeedByEntityId(int userId, int entityId) {
        update(DELETE_BY_ENTITYID, entityId);

        log.trace(String.format("deleted events for userId=%d, entityId=%d", userId, entityId));
    }

    public void addFeed(Feed feed) {
        insert(ADD_QUERY,
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEventType(),
                feed.getOperation(),
                feed.getEntityId());

        log.trace("Add event " + feed);
    }
}
