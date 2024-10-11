package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.CorruptedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService service;

//    @GetMapping
//    public Collection<ReviewDto> getReviews() throws NotFoundException {
//        return service.getReviews();
//    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable int id) throws NotFoundException {
        return service.getReview(id);
    }

    @GetMapping
    public Collection<ReviewDto> getMostPopular(
            @RequestParam int filmId,
            @RequestParam(required = false, defaultValue = "10") int count) {
        return service.getMostPopular(filmId, count);
    }

    @PostMapping
    public ReviewDto createReview(@Valid @RequestBody ReviewDto reviewDto) throws CorruptedDataException, NotFoundException {
        return service.addReview(reviewDto);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto reviewDto) throws NotFoundException, CorruptedDataException {
        return service.updateReview(reviewDto);
    }

    @DeleteMapping("/{id}")
    public ReviewDto deleteReview(@PathVariable int id) throws NotFoundException {
        return service.deleteReview(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLikeReview(@PathVariable("id") int reviewId, @PathVariable int userId) throws NotFoundException {
        service.addLike(reviewId, userId, 1);
        return service.getReview(reviewId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislikeReview(@PathVariable("id") int reviewId, @PathVariable int userId) throws NotFoundException {
        service.addLike(reviewId, userId, -1);
        return service.getReview(reviewId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLikeReview(@PathVariable("id") int reviewId, @PathVariable int userId) throws NotFoundException {
        service.deleteLike(reviewId, userId);
        return service.getReview(reviewId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislikeReview(@PathVariable("id") int reviewId, @PathVariable int userId) throws NotFoundException {
        service.deleteLike(reviewId, userId);
        return service.getReview(reviewId);
    }
}
