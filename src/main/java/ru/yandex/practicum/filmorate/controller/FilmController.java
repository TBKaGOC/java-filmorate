package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping
    public Collection<FilmDto> getFilms() throws NotFoundException {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable int id) throws NotFoundException {
        return service.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopular(@RequestParam(required = false, defaultValue = "10") int count,
                                              @RequestParam(required = false) Integer genreId,
                                              @RequestParam(required = false) Integer year) {
        return service.getMostPopular(count, genreId, year);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmDto film) throws CorruptedDataException, NotFoundException {
        service.addFilm(film);
        return service.getFilm(film.getId());
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto film) throws NotFoundException, CorruptedDataException {
        return service.updateFilm(film);
    }

    @PutMapping("/{film_id}/like/{id}")
    public FilmDto likeFilm(@PathVariable("film_id") int filmId, @PathVariable int id) throws NotFoundException {
        service.addLike(id, filmId);
        return service.getFilm(filmId);
    }

    @DeleteMapping("/{film_id}/like/{id}")
    public void unlikeFilm(@PathVariable("film_id") int filmId, @PathVariable int id) throws NotFoundException {
        service.deleteLike(id, filmId);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(
            @RequestParam int userId,
            @RequestParam int friendId) {
        return service.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> findDirectorFilms(@PathVariable("directorId") int directorId,
                                                 @RequestParam(name = "sortBy", defaultValue = "")
                                                 String sortConditions) throws NotFoundException {
        return service.findDirectorFilms(directorId, sortConditions);
    }
}
