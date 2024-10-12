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
        FilmDto filmDto = service.getFilm(id);
        return filmDto;
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopular(@RequestParam(required = false, defaultValue = "10") String count) {
        return service.getMostPopular(count);
    }

    @PostMapping
    public FilmDto createFilm(@Valid @RequestBody FilmDto film) throws CorruptedDataException, NotFoundException {
        service.addFilm(film);
        FilmDto filmDto = service.getFilm(film.getId());
        return filmDto;
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

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        service.deleteFilm(filmId);
    }

    @DeleteMapping("/{film_id}/like/{id}")
    public void unlikeFilm(@PathVariable("film_id") int filmId, @PathVariable int id) throws NotFoundException {
        service.deleteLike(id, filmId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> findDirectorFilms(@PathVariable("directorId") int directorId,
                                                 @RequestParam(name = "sortBy", defaultValue = "")
                                                 String sortConditions) throws NotFoundException {
        return service.findDirectorFilms(directorId, sortConditions);
    }
}
