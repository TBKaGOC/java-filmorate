package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class UserStorageTests {
    private UserService service;
    private UserStorage userStorage;

    @BeforeEach
    public void createNewUserController() {
        userStorage = new InMemoryUserStorage();
        service = new UserService(userStorage, new UserMapper());
    }

    @Test
    public void shouldWeGetAllUsers() throws DuplicatedDataException, NotFoundException {
        Collection<User> userCollection = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            User newUser = User.builder()
                    .login("login" + i)
                    .name("name")
                    .email("rightemail" + i + "@email.right")
                    .birthday(LocalDate.of(2000, 1, 1))
                    .build();

            userStorage.addUser(newUser);
            userCollection.add(newUser);
        }

        Assertions.assertTrue(userStorage.getUsers().containsAll(userCollection));
    }

    @Test
    public void shouldWeCreateNewUser() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();



        userStorage.addUser(newUser);
        User createdUser = userStorage.getUser(1);
        newUser.setId(createdUser.getId());

        Assertions.assertEquals(createdUser, newUser);
    }

    @Test
    public void shouldWeCreateNewUserWithBlankName() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .name("")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userStorage.addUser(newUser);

        User createdUser = userStorage.getUser(1);
        Assertions.assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    public void shouldWeCreateNewUserWithNullName() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userStorage.addUser(newUser);
        User createdUser = userStorage.getUser(1);

        Assertions.assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    public void shouldWeGetExceptionWithDuplicateEmail() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userStorage.addUser(newUser);
        User newUser2 = User.builder()
                .login("newLogin")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Assertions.assertThrowsExactly(DuplicatedDataException.class, () -> userStorage.addUser(newUser2));
    }

    @Test
    public void shouldWeGetExceptionWithDuplicateLogin() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userStorage.addUser(newUser);
        User newUser2 = User.builder()
                .login("login")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Assertions.assertThrowsExactly(DuplicatedDataException.class, () -> userStorage.addUser(newUser2));
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
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);

        service.addFriend(user3.getId(), user1.getId());
        service.addFriend(user3.getId(), user2.getId());

        Assertions.assertTrue(user1.getFriends().contains(user3.getId()));
        Assertions.assertTrue(user2.getFriends().contains(user3.getId()));

        Assertions.assertTrue(userStorage.getMutualFriend(user1.getId(), user2.getId()).contains(user3));
    }
}
