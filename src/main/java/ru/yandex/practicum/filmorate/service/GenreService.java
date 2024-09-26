package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage storage;

    public Collection<GenreDto> getGenres() throws NotFoundException {
        List<GenreDto> list = new ArrayList<>();
        for (Genre genre : storage.getGenres()) {
            GenreDto genreDto = GenreMapper.mapToGenreDto(genre);
            list.add(genreDto);
        }
        return list;
    }

    public GenreDto getGenre(Integer id) throws NotFoundException {
        return GenreMapper.mapToGenreDto(storage.getGenre(id));
    }
}
