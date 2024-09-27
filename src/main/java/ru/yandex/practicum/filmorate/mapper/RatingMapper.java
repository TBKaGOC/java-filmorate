package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

@Component
public class RatingMapper {

    public RatingDto mapToRatingDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .name(rating.getName())
                .build();
    }

    public Rating mapToRating(RatingDto ratingDto) {
        return Rating.builder()
                .id(ratingDto.getId())
                .name(ratingDto.getName())
                .build();
    }
}
