package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingDbStorage storage;

    public Collection<RatingDto> getRatings() {
        return storage.getRatings().stream().map(RatingMapper::mapToRatingDto).collect(Collectors.toList());
    }

    public RatingDto getRating(Integer id) throws NotFoundException {
        return RatingMapper.mapToRatingDto(storage.getRating(id));
    }
}
