package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewMapper {
    public ReviewDto mapToReviewDto(Review review) {
        if (review == null)
            return null;

        var useful = 0;

        if (review.getUseful() != null)
            useful = review.getUseful();

        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.getIsPositive())
                .useful(useful)
                .userId(review.getUserId())
                .filmId(review.getFilmId())
                .build();
    }

    public Review mapToReview(ReviewDto reviewDto) {
        if (reviewDto == null)
            return null;

        return Review.builder()
                .reviewId(reviewDto.getReviewId())
                .content(reviewDto.getContent())
                .isPositive(reviewDto.getIsPositive())
                .filmId(reviewDto.getFilmId())
                .userId(reviewDto.getUserId())
                .build();
    }
}
