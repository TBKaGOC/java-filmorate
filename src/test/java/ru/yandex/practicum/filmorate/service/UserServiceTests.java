package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.in_memory.InMemoryUserStorage;

import java.time.LocalDate;

public class UserServiceTests {
    private UserService service;

    @BeforeEach
    public void createNewService() {
        service = new UserService(new InMemoryUserStorage(), new UserMapper(), new FeedMapper());
    }

    //@Test
    public void shouldWeAddFriend() throws NotFoundException, DuplicatedDataException {
        UserDto user1 = UserDto.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addUser(user1);
        service.addUser(user2);
        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user2.getId(), user1.getId());

        user1 = service.getUser(user1.getId());
        user2 = service.getUser(user2.getId());

        Assertions.assertTrue(user1.getFriends().contains(user2.getId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getId()));
    }

    //@Test
    public void shouldWeDoNotAddTwoSameFriends() throws NotFoundException, DuplicatedDataException {
        UserDto user1 = UserDto.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addUser(user1);
        service.addUser(user2);

        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user2.getId(), user1.getId());

        user1 = service.getUser(user1.getId());
        user2 = service.getUser(user2.getId());

        Assertions.assertEquals(user1.getFriends().size(), 1);
        Assertions.assertEquals(user2.getFriends().size(), 1);
    }

    @Test
    public void shouldWeUpdateUser() throws DuplicatedDataException, NotFoundException {
        UserDto newUser = UserDto.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        service.addUser(newUser);
        UserDto newUser2 = UserDto.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();
        service.updateUser(newUser2);
        UserDto updateUser = service.getUser(newUser2.getId());

        Assertions.assertEquals(newUser2.getId(), updateUser.getId());
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithDuplicateEmail() throws DuplicatedDataException, NotFoundException {
        UserDto newUser = UserDto.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();
        UserDto newUser2 = UserDto.builder()
                .login("login2")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        service.addUser(newUser);
        service.addUser(newUser2);

        UserDto userForUpdate = UserDto.builder()
                .id(newUser.getId())
                .login("login3")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(DuplicatedDataException.class, () -> service.updateUser(userForUpdate));
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithDuplicateLogin() throws DuplicatedDataException, NotFoundException {
        UserDto newUser = UserDto.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();
        UserDto newUser2 = UserDto.builder()
                .login("login2")
                .email("rightemail2@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        service.addUser(newUser);
        service.addUser(newUser2);

        UserDto userForUpdate = UserDto.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(DuplicatedDataException.class, () -> service.updateUser(userForUpdate));
    }

    @Test
    public void shouldWeRightUpdateNameWhenNameIsBlankAndNull() throws DuplicatedDataException, NotFoundException {
        UserDto newUser = UserDto.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("")
                .build();

        service.addUser(newUser);
        UserDto newUser2 = UserDto.builder()
                .id(newUser.getId())
                .login("login2")
                .email("newrightemail2@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .build();
        service.updateUser(newUser2);
        UserDto updateUser = service.getUser(newUser2.getId());

        Assertions.assertEquals(updateUser.getName(), newUser2.getLogin());
    }

    @Test
    public void shouldWeGetExceptionWhenUpdateUserWithNewId() throws DuplicatedDataException, NotFoundException {
        UserDto newUser = UserDto.builder()
                .login("login")
                .email("rightemail@email.right")
                .birthday(LocalDate.of(2000, 1, 1))
                .name("name")
                .build();

        service.addUser(newUser);

        UserDto userForUpdate = UserDto.builder()
                .id(newUser.getId() + 1)
                .login("login2")
                .email("newrightemail@email.right")
                .birthday(LocalDate.of(2003, 4, 23))
                .name("name2")
                .build();

        Assertions.assertThrows(NotFoundException.class, () -> service.updateUser(userForUpdate));
    }

    //@Test
    public void shouldWeDeleteFriend() throws NotFoundException, DuplicatedDataException {
        UserDto user1 = UserDto.builder()
                .id(1)
                .name("user1")
                .login("user1")
                .email("email@email.e")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2)
                .name("user2")
                .login("user2")
                .email("email2@email.e")
                .build();

        service.addUser(user1);
        service.addUser(user2);

        service.addFriend(user1.getId(), user2.getId());
        service.addFriend(user2.getId(), user1.getId());

        user1 = service.getUser(user1.getId());
        user2 = service.getUser(user2.getId());

        Assertions.assertTrue(user1.getFriends().contains(user2.getId()));
        Assertions.assertTrue(user2.getFriends().contains(user1.getId()));

        service.deleteFriend(user1.getId(), user2.getId());

        user1 = service.getUser(user1.getId());
        user2 = service.getUser(user2.getId());

        Assertions.assertFalse(user1.getFriends().contains(user2.getId()));
        Assertions.assertFalse(user2.getFriends().contains(user1.getId()));
    }
}
