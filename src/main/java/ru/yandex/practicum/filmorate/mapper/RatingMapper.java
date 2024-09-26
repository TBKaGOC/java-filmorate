package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.RatingDbStorage;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RatingMapper {
    private static RatingDbStorage storage;

    public static RatingDto mapToRatingDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .name(rating.getName())
                .build();
    }

    public static Rating mapToRating(RatingDto ratingDto) {
        return Rating.builder()
                .id(ratingDto.getId())
                .name(ratingDto.getName())
                .build();
    }
}
