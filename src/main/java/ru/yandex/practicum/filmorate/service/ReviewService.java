package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final ReviewMapper reviewMapper;

    public ReviewDto getReview(int id) throws NotFoundException {
        return reviewMapper.mapToReviewDto(storage.getReview(id));
    }

    public Collection<ReviewDto> getMostPopular(Integer filmId, int limit) {
        if (filmId == null) {
            return storage.getReviews(limit)
                    .stream()
                    .map(reviewMapper::mapToReviewDto)
                    .collect(Collectors.toList());
        }
        return storage
                .getMostPopularReviews(filmId, limit)
                .stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto addReview(@Valid ReviewDto reviewDto) throws NotFoundException {
        log.info("Request to add review \"{}\"", reviewDto);

        var filmId = reviewDto.getFilmId();

        if (filmId == null) {
            log.warn("Не удалось найти фильма \"null\"");
            throw new ValidationException("Фильм \"null\" не найден");
        } else if (!storage.contains(filmId)) {
            log.warn("Не удалось найти фильма {}", filmId);
            throw new NotFoundException(String.format("Фильм \"%d\" не найден", filmId));
        }

        var userId = reviewDto.getUserId();

        if (userId == null) {
            log.warn("Не удалось найти пользователя \"null\"");
            throw new ValidationException("Пользователь \"null\" не найден");
        } else if (!userStorage.contains(userId)) {
            log.warn("Не удалось найти пользователя {}", userId);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", userId));
        }

        var id = storage.addReview(reviewMapper.mapToReview(reviewDto));

        var result = reviewMapper.mapToReviewDto(storage.getReview(id));

        log.info("Успешно добавлен новый отзыв \"{}\"", result);

        return result;
    }

    public ReviewDto updateReview(ReviewDto reviewDto) throws NotFoundException {
        log.info("Request to update review \"{}\"", reviewDto);

        var id = reviewDto.getReviewId();
        Review oldReview = storage.getReview(id);
        if (oldReview == null) {
            log.warn("Не удалось найти отзыв {}", id);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", id));
        }
        reviewDto.setFilmId(oldReview.getFilmId());
        reviewDto.setUserId(oldReview.getUserId());

        storage.updateReview(reviewMapper.mapToReview(reviewDto));

        var result = reviewMapper.mapToReviewDto(storage.getReview(id));

        log.info("Success to update review \"{}\"", result);

        return result;
    }

    public ReviewDto deleteReview(int id) throws NotFoundException {
        log.info("Request to delete reviewId \"{}\"", id);

        if (!storage.containsReview(id)) {
            log.warn("Не удалось найти отзыв {}", id);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", id));
        }

        var deletedReview = storage.getReview(id);

        storage.deleteReview(id);

        var result = reviewMapper.mapToReviewDto(deletedReview);

        log.trace("Success to delete reviewId \"{}\"", id);

        return result;
    }

    public void addLike(int reviewId, int userid, int useful) throws NotFoundException {
        log.trace("Request to add like to reviewId \"{}\" from userId \"{}\" as useful \"{}\"",
                        reviewId,
                        userid,
                        useful);

        if (!storage.containsReview(reviewId)) {
            log.warn("Не удалось найти отзыв {}", reviewId);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", reviewId));
        }

        if (!userStorage.contains(userid)) {
            log.warn("Не удалось найти пользователя {}", userid);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", userid));
        }

        if (storage.containsReviewLike(reviewId, userid)) {
            storage.updateReviewLike(reviewId, userid, useful);
        } else {
            storage.addReviewLike(reviewId, userid, useful);
        }

        log.info("Success to add like to reviewId \"{}\" from userId \"{}\" as useful \"{}\"",
                reviewId,
                userid,
                useful);
    }

    public void deleteLike(int reviewId, int userid) throws NotFoundException {
        log.info("Request to delete like to reviewId \"{}\" from userId \"{}\"", reviewId, userid);

        if (!storage.containsReview(reviewId)) {
            log.warn("Не удалось найти отзыв {}", reviewId);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", reviewId));
        }

        if (!userStorage.contains(userid)) {
            log.warn("Не удалось найти пользователя {}", userid);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", userid));
        }

        if (!storage.containsReviewLike(reviewId, userid)) {
            log.warn("Не удалось найти лайк/дизлайк пользователя {} для отзыва {}", userid, reviewId);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден среди лайка/дизлайка для отзыва %d", userid, reviewId));
        }

        storage.deleteReviewLike(reviewId, userid);

        log.info("Success to delete like to reviewId \"{}\" from userId \"{}\"", reviewId, userid);
    }
}
