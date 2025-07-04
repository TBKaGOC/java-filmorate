package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTests {
    private final UserDbStorage storage;

    @Test
    public void testGetAllUser() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        Collection<User> users = storage.getUsers();

        Assertions.assertFalse(users.isEmpty());
    }

    @Test
    public void testGetUser() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        User user1 = storage.getUser(user.getId());

        Assertions.assertNotNull(user1);
        Assertions.assertEquals(user1.getId(), user.getId());
    }

    @Test
    public void testGetFriends() throws NotFoundException {
        User user1 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        User user2 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        User user3 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user1);
        storage.addUser(user2);
        storage.addUser(user3);
        storage.addFriend(user3, user1, false);
        storage.addFriend(user2, user1, false);
        storage.addFriend(user1, user3, true);
        storage.addFriend(user1, user2, true);

        Collection<User> friends = storage.getFriends(user1.getId());

        Assertions.assertNotNull(friends);
        Assertions.assertTrue(friends.containsAll(List.of(storage.getUser(user2.getId()),
                storage.getUser(user3.getId()))));
    }

    @Test
    public void testGetMutualFriends() throws NotFoundException {
        User user1 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        User user2 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        User user3 = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user1);
        storage.addUser(user2);
        storage.addUser(user3);
        storage.addFriend(user3, user1, false);
        storage.addFriend(user3, user2, false);
        storage.addFriend(user1, user3, true);
        storage.addFriend(user2, user3, true);

        Collection<User> mutualFriends = storage.getMutualFriend(user1.getId(), user2.getId());

        Assertions.assertNotNull(mutualFriends);
        Assertions.assertTrue(mutualFriends.contains(storage.getUser(user3.getId())));
    }

    @Test
    public void testAddUser() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();

        storage.addUser(user);

        Assertions.assertTrue(storage.contains(user.getId()));
    }

    //@Test
    public void testAddFriend() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        storage.addFriend(user, storage.getUser(1), false);
        storage.addFriend(user, storage.getUser(1), true);

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertTrue(storage.getFriends(1).contains(user));
    }

    //@Test
    public void testAddFriendNotConfirmed() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        storage.addFriend(user, storage.getUser(1), false);

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));
    }

    @Test
    public void testUpdateUser() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);

        User user2 = User.builder()
                .id(user.getId())
                .email("new@mail.e")
                .login("new_login")
                .name("new_name")
                .birthday(LocalDate.now().minusDays(23))
                .friends(new HashMap<>())
                .build();
        storage.updateUser(user2);

        User newUser = storage.getUser(user2.getId());

        Assertions.assertEquals(user2.getEmail(), newUser.getEmail());
        Assertions.assertEquals(user2.getLogin(), newUser.getLogin());
        Assertions.assertEquals(user2.getName(), newUser.getName());
        Assertions.assertEquals(user2.getBirthday(), newUser.getBirthday());
    }

    @Test
    public void testDeleteUser() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();

        storage.addUser(user);

        Assertions.assertTrue(storage.contains(user.getId()));

        storage.deleteUser(user.getId());

        Assertions.assertFalse(storage.contains(user.getId()));
    }

   //@Test
    public void testDeleteFriend() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        storage.addFriend(user, storage.getUser(1), false);
        storage.addFriend(user, storage.getUser(1), true);

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertTrue(storage.getFriends(1).contains(user));

        storage.deleteFriend(user.getId(), 1);
        storage.deleteFriend(1, user.getId());

        Assertions.assertFalse(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));
    }

    //@Test
    public void testDeleteFriendNotConfirmed() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        storage.addFriend(user, storage.getUser(1), false);

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));

        storage.deleteFriend(user.getId(), 1);

        Assertions.assertFalse(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));
    }

    //@Test
    public void testNotDeleteFriendNotConfirmed() throws NotFoundException {
        User user = User.builder()
                .email("e@mail.e")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .friends(new HashMap<>())
                .build();
        storage.addUser(user);
        storage.addFriend(user, storage.getUser(1), false);

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));

        storage.deleteFriend(1, user.getId());

        Assertions.assertTrue(storage.getFriends(user.getId()).contains(storage.getUser(1)));
        Assertions.assertFalse(storage.getFriends(1).contains(user));
    }
}
