package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

public class UserServiceTests {
    private UserService service;
    private UserStorage storage;

    @BeforeEach
    public void createNewService() {
        storage = new InMemoryUserStorage();
        service = new UserService(storage);
    }

    @Test
    public void shouldWeAddFriend() {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addFriend(user1, user2);

        Assertions.assertTrue(user1.getFriends().contains(user2.getId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    public void shouldWeDoNotAddTwoSameFriends() {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addFriend(user1, user2);
        service.addFriend(user2, user1);

        Assertions.assertEquals(user1.getFriends().size(), 1);
        Assertions.assertEquals(user2.getFriends().size(), 1);
    }

    @Test
    public void shouldWeDeleteFriend() {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addFriend(user1, user2);

        Assertions.assertTrue(user1.getFriends().contains(user2.getId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getId()));

        service.deleteFriend(user1, user2);

        Assertions.assertFalse(user1.getFriends().contains(user2.getId()));
        Assertions.assertFalse(user2.getFriends().contains(user1.getId()));
    }

    @Test
    public void shouldWeGetMutualFriendsOfTwoUsers() throws NotFoundException, DuplicatedDataException {
        User user1 = User.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        User user2 = User.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();
        User user3 = User.builder()
                .id(3)
                .name("user3")
                .login("user3")
                .email("email3@email.e")
                .build();
        storage.addUser(user3);

        service.addFriend(user3, user1);
        service.addFriend(user3, user2);

        Assertions.assertTrue(user3.getFriends().contains(user1.getId()));
        Assertions.assertTrue(user3.getFriends().contains(user2.getId()));

        Assertions.assertTrue(service.getMutualFriend(user1, user2).contains(user3));
    }
}
