package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.Collection;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTests {
    private final GenreDbStorage storage;

    @Test
    public void testGetAllFilms() {
        Collection<Genre> genres = storage.getGenres();

        Assertions.assertFalse(genres.isEmpty());
    }

    @Test
    public void testGetFilm() throws NotFoundException {
        Genre genre = storage.getGenre(1);

        Assertions.assertNotNull(genre);
        Assertions.assertEquals(genre.getId(), 1);
    }
}
