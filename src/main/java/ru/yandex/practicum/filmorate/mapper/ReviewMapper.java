package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewType;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewMapper {
    public ReviewDto mapToReviewDto(Review review) {
        var useful = 0;

        if (review.getUseful() != null)
            useful = review.getUseful();

        return ReviewDto.builder()
                .id(review.getReviewId())
                .content(review.getContent())
                .reviewType(ReviewType.valueOf(review.getReviewType()))
                .useful(useful)
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .build();
    }

    public Review mapToReview(ReviewDto reviewDto) {
        return Review.builder()
                .reviewId(reviewDto.getId())
                .content(reviewDto.getContent())
                .reviewType(reviewDto.getReviewType().name())
                .filmId(reviewDto.getFilmId())
                .userId(reviewDto.getUserId())
                .build();
    }
}
