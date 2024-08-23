package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService service;

    @GetMapping
    public Collection<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) throws NotFoundException {
        return service.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(required = false, defaultValue = "10") String count) {
        return service.getMostPopular(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws CorruptedDataException {
        service.addFilm(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws NotFoundException, CorruptedDataException {
        service.updateFilm(film);
        return service.getFilm(film.getId());
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) throws NotFoundException {
        service.addLike(userId, id);
        return service.getFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable int id, @PathVariable int userId) throws NotFoundException {
        service.deleteLike(userId, id);
    }
}
