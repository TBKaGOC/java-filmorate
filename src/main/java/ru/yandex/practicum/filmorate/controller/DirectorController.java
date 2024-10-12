package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping("/{id}")
    public DirectorDto findDirector(@PathVariable("id") int directorId) throws NotFoundException {
        return directorService.findDirector(directorId);
    }

    @GetMapping
    public Collection<DirectorDto> findAll() {
        return directorService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody DirectorDto directorDto) throws DuplicatedDataException {
        return directorService.create(directorDto);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody DirectorDto directorDto) throws NotFoundException {
        return directorService.update(directorDto);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable("id") int ratingId) throws NotFoundException {
        return directorService.delete(ratingId);
    }
}
