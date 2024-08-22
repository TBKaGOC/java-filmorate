package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmStorage storage;
    private final FilmService service;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) throws NotFoundException {
        return storage.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(required = false, defaultValue = "10") String count) {
        service.setSizeOfTop(Integer.parseInt(count));
        return service.getMostPopular(storage);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws CorruptedDataException {
        storage.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException, CorruptedDataException {
        storage.updateFilm(film);
        return storage.getFilm(film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) throws NotFoundException {
        Film film = storage.getFilm(id);
        userStorage.findUser(userId);
        service.addLike(userId, film);
        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) throws NotFoundException {
        userStorage.findUser(userId);
        service.deleteLike(userId, storage.getFilm(id));
    }
}
