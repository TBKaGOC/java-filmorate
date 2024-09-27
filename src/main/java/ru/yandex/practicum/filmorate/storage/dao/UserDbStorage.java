package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Component
@Slf4j
@Primary
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_QUERY = "SELECT * FROM users WHERE id IN (SELECT sender AS id FROM friends WHERE recipient = ? UNION SELECT recipient AS id FROM friends WHERE sender = ? AND confirmed = TRUE)";
    private static final String FIND_MUTUAL_FRIEND_QUERY = "SELECT * FROM users WHERE id IN (SELECT recipient AS id FROM friends WHERE sender = ? AND recipient IN (SELECT recipient AS id FROM friends WHERE sender = ? UNION SELECT sender AS id FROM friends WHERE recipient = ?) UNION SELECT sender AS id FROM friends WHERE recipient = ? AND sender IN (SELECT recipient AS id FROM friends WHERE sender = ? UNION SELECT sender AS id FROM friends WHERE recipient = ?))";
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
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE recipient = ? AND sender = ?" +
            "OR sender = ? AND recipient = ? AND confirmed = ?";
    private static final String CONTAINS_QUERY = "SELECT EXISTS(SELECT id FROM users WHERE id = ?) AS b";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
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
        return findMany(FIND_FRIENDS_QUERY, id, id);
    }

    @Override
    public Set<User> getMutualFriend(Integer id1, Integer id2) {
        return Set.copyOf(findMany(FIND_MUTUAL_FRIEND_QUERY, id1, id2, id2, id1, id2, id2));
    }

    @Override
    public Integer addUser(User user) throws NotFoundException {
        int id = (int) insert(ADD_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);


        for (Integer friendId: user.getFriends()) {
            User friend = getUser(friendId);

            if (friend.getFriends().contains(id)) {
                update(UPDATE_FRIENDS_STATUS, true, id, friendId, friendId, id);
            } else {
                update(ADD_FRIEND_QUERY, id, friendId, user.isFriendConfirm(friendId));
            }
        }
        return user.getId();
    }

    @Override
    public void addFriend(User recipient, User sender, Boolean confirmed) {
        int id = sender.getId();
        int friendId = recipient.getId();
        if (confirmed) {
            update(UPDATE_FRIENDS_STATUS, true, id, friendId, friendId, id);
        } else {
            update(ADD_FRIEND_QUERY, id, friendId, false);
        }
    }

    @Override
    public void updateUser(User user) {
        int id = user.getId();
        update(UPDATE_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
    }

    @Override
    public void deleteUser(Integer id) {
        delete(DELETE_QUERY, id);
    }

    @Override
    public void deleteFriend(Integer recipient, Integer sender) throws NotFoundException {
        if (getFriends(recipient).contains(getUser(sender)) || getFriends(sender).contains(getUser(recipient))) {
            update(DELETE_FRIEND_QUERY, recipient, sender, recipient, sender, true);
        }
    }

    @Override
    public boolean contains(Integer id) {
        return jdbc.queryForList(CONTAINS_QUERY, Boolean.class, id).getFirst();
    }
}
