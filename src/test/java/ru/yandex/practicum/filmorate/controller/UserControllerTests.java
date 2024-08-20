package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

public class UserControllerTests {
    private UserController userController;

    @BeforeEach
    public void createNewUserController() {
        userController = new UserController();
    }

    @Test
    public void shouldWeGetAllUsers() throws DuplicatedDataException {
        Collection<User> userCollection = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            User newUser = User.builder()
                    .login("login" + i)
                    .name("name")
                    .email("rightemail" + i + "@email.right")
                    .birthday(LocalDate.of(2000, 1, 1))
                    .build();

            userController.createUser(newUser);
            userCollection.add(newUser);
        }

        Assertions.assertTrue(userController.getUsers().containsAll(userCollection));
    }

    @Test
    public void shouldWeCreateNewUser() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .name("name")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User createdUser = userController.createUser(newUser);
        newUser.setId(createdUser.getId());

        Assertions.assertEquals(createdUser, newUser);
    }

    @Test
    public void shouldWeCreateNewUserWithBlankName() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .name("")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User createdUser = userController.createUser(newUser);

        Assertions.assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    public void shouldWeCreateNewUserWithNullName() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User createdUser = userController.createUser(newUser);

        Assertions.assertEquals(createdUser.getName(), createdUser.getLogin());
    }

    @Test
    public void shouldWeGetExceptionWithDuplicateEmail() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.createUser(newUser);
        User newUser2 = User.builder()
                .login("newLogin")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Assertions.assertThrowsExactly(DuplicatedDataException.class, () -> userController.createUser(newUser2));
    }

    @Test
    public void shouldWeGetExceptionWithDuplicateLogin() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.createUser(newUser);
        User newUser2 = User.builder()
                .login("login")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        Assertions.assertThrowsExactly(DuplicatedDataException.class, () -> userController.createUser(newUser2));
    }

    @Test
    public void shouldWeUpdateUser() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        userController.createUser(newUser);
        User newUser2 = User.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();
        User updateUser = userController.updateUser(newUser2);

        Assertions.assertEquals(newUser2, updateUser);
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithDuplicateEmail() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();
        User newUser2 = User.builder()
                .login("login2")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        userController.createUser(newUser);
        userController.createUser(newUser2);

        User userForUpdate = User.builder()
                .id(newUser.getId())
                .login("login3")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(DuplicatedDataException.class, () -> userController.updateUser(userForUpdate));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithDuplicateLogin() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();
        User newUser2 = User.builder()
                .login("login2")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        userController.createUser(newUser);
        userController.createUser(newUser2);

        User userForUpdate = User.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(DuplicatedDataException.class, () -> userController.updateUser(userForUpdate));
    }

    @Test
    public void shouldWeRightUpdateNameWhenNameIsBlankAndNull() throws DuplicatedDataException, NotFoundException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("")
                .build();

        userController.createUser(newUser);
        User newUser2 = User.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .build();
        User updateUser = userController.updateUser(newUser2);

        Assertions.assertEquals(updateUser.getName(), newUser2.getLogin());
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithNewId() throws DuplicatedDataException {
        User newUser = User.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        userController.createUser(newUser);

        User userForUpdate = User.builder()
                .id(newUser.getId() + 1)
                .login("login2")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> userController.updateUser(userForUpdate));
    }
}
