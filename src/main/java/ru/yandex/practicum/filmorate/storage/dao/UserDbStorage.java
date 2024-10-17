package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FeedEventType;
import ru.yandex.practicum.filmorate.dto.FeedOperationType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY =
            "SELECT u.* " +
            "FROM friends f " +
            "   JOIN users u on u.id = f.recipient " +
            "WHERE f.sender = ?";
    private static final String FIND_MUTUAL_FRIEND_QUERY =
            "SELECT u.* " +
            "FROM users u " +
            "   JOIN(SELECT recipient " +
            "        FROM friends " +
            "        WHERE sender = ? " +
            "        INTERSECT " +
            "        SELECT recipient " +
            "        FROM friends " +
            "        WHERE sender = ? " +
            "       ) f on f.recipient = u.id";
    private static final String ADD_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";

    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (sender, recipient, confirmed) " +
                                                   "VALUES (?, ?, ?)";

    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String UPDATE_FRIENDS_STATUS = "UPDATE friends SET confirmed = ? " +
                                                        "WHERE sender = ? AND recipient = ?" +
                                                        "OR sender = ? AND recipient = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String DELETE_FROM_FRIEND_QUERY = "DELETE FROM friends WHERE recipient = ? or sender = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE recipient = ? AND sender = ?";
    private static final String CONTAINS_QUERY = "SELECT EXISTS(SELECT id FROM users WHERE id = ?) AS b";

    private final FeedDbStorage feedDbStorage;

    public UserDbStorage(
            JdbcTemplate jdbc,
            RowMapper<User> mapper,
            FeedDbStorage feedDbStorage) {
        super(jdbc, mapper);

        this.feedDbStorage = feedDbStorage;
    }

    @Override
    public Collection<User> getUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User getUser(Integer id) throws NotFoundException {
        try {
            Collection<User> friends = getFriends(id);
            User user = findOne(FIND_BY_ID_QUERY, id)
                    .orElseThrow(() -> new NotFoundException("Не найден пользователь " + id));

            for (User friend : friends) {
                user.addFriend(friend, true);
            }

            return user;
        } catch (NotFoundException e) {
            log.warn("Не удалось получить пользователя {}", id);
            throw e;
        }
    }

    @Override
    public Collection<User> getFriends(Integer id) {
        return findMany(FIND_FRIENDS_QUERY, id);
    }

    @Override
    public Set<User> getMutualFriend(Integer id1, Integer id2) {
        return Set.copyOf(findMany(FIND_MUTUAL_FRIEND_QUERY, id1, id2));
    }

    @Override
    public Integer addUser(User user) throws NotFoundException {
        int id = (int) insert(ADD_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);


        for (Integer friendId: user.getFriends()) {
            User friend = getUser(friendId);

            if (friend.getFriends().contains(id) && friend.isFriendConfirm(id)) {
                update(UPDATE_FRIENDS_STATUS, true, id, friendId, friendId, id);
            } else {
                update(ADD_FRIEND_QUERY, id, friendId, user.isFriendConfirm(friendId));
            }
        }
        return user.getId();
    }

    @Override
    public void addFriend(User recipient, User sender, Boolean confirmed) {
        if (recipient == null || sender == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        int id = sender.getId();
        int friendId = recipient.getId();

        if (confirmed) {
            jdbc.update(UPDATE_FRIENDS_STATUS, true, id, friendId, friendId, id);
        } else {
            jdbc.update(ADD_FRIEND_QUERY, id, friendId, true);
        }

        feedDbStorage.addFeed(Feed.builder()
                .userId(id)
                .timestamp(new Date().getTime())
                .eventType(FeedEventType.FRIEND.name())
                .operation(FeedOperationType.ADD.name())
                .entityId(friendId)
                .build());
    }

    @Override
    public void updateUser(User user) {
        int id = user.getId();
        update(UPDATE_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
    }

    @Override
    public void deleteUser(Integer id) {
        feedDbStorage.deleteFeedByUserId(id);

        update(DELETE_FROM_FRIEND_QUERY, id, id);
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteFriend(Integer recipient, Integer sender) throws NotFoundException {
        if (getFriends(sender).contains(getUser(recipient))) {
            update(DELETE_FRIEND_QUERY, recipient, sender);

            feedDbStorage.addFeed(Feed.builder()
                    .userId(sender)
                    .timestamp(new Date().getTime())
                    .eventType(FeedEventType.FRIEND.name())
                    .operation(FeedOperationType.REMOVE.name())
                    .entityId(recipient)
                    .build());
        }
    }

    @Override
    public boolean contains(Integer id) {
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }

    @Override
    public Collection<Feed> getFeeds(int userId) {
        return feedDbStorage.getFeedByUserId(userId);
    }
}
