package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

public class DirectorMapper {

    public static DirectorDto mapToDirectorDto(Director director) {
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }

    public static Director mapToDirector(DirectorDto request) {
        Director director = new Director();
        director.setName(request.getName());

        return director;
    }

    public static Director updateDirectorFields(Director director, DirectorDto request) {
        director.setName(request.getName());
        return director;
    }
}
