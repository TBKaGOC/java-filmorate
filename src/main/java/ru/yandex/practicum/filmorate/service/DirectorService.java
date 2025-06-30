package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dao.DirectorDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {

    private final DirectorDbStorage directorStorage;

    public DirectorDto findDirector(int directorId) throws NotFoundException {
        return DirectorMapper.mapToDirectorDto(directorStorage.findDirector(directorId));
    }

    public Collection<DirectorDto> findAll() {
        log.info("Получаем записи о всех режиссерах");
        return directorStorage.findAll().stream().map(DirectorMapper::mapToDirectorDto).collect(Collectors.toList());
    }

    public DirectorDto create(DirectorDto request) throws DuplicatedDataException {
        log.info("Создаем запись о режиссере");

        if (directorStorage.isDirectorWithSameNameExist(request.getName())) {
            throw new DuplicatedDataException(String.format("Режиссер с именем \"%s\" уже существует",
                    request.getName()));
        }

        Director director = DirectorMapper.mapToDirector(request);
        director = directorStorage.create(director);

        return DirectorMapper.mapToDirectorDto(director);
    }

    public DirectorDto update(DirectorDto request) throws NotFoundException {
        log.info("Обновляем данные о режиссерах");

        if (request.getId() == null) {
            throw new ValidationException("Id режиссера должен быть указан");
        }

        Director updatedDirector = DirectorMapper.updateDirectorFields(directorStorage
                .findDirector(request.getId()), request);
        updatedDirector = directorStorage.update(updatedDirector);

        return DirectorMapper.mapToDirectorDto(updatedDirector);
    }

    public boolean delete(int directorId) throws NotFoundException {
        Director director = directorStorage.findDirector(directorId);

        log.info("Удаляем данные режиссера {}", director.getName());

        return directorStorage.delete(directorId);
    }
}
