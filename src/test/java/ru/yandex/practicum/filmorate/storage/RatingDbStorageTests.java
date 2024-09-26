package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.util.Collection;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RatingDbStorageTests {
    private final RatingDbStorage storage;

    @Test
    public void testGetAllFilms() {
        Collection<Rating> ratings = storage.getRatings();

        Assertions.assertFalse(ratings.isEmpty());
    }

    @Test
    public void testGetFilm() throws NotFoundException {
        Rating rating = storage.getRating(1);

        Assertions.assertNotNull(rating);
        Assertions.assertEquals(rating.getId(), 1);
    }
}
