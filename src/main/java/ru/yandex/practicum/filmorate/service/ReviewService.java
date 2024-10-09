package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
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

    public Collection<ReviewDto> getMostPopular(int filmId, int count) {
        return storage
                .getMostPopularReviews(filmId, count)
                .stream()
                .map(reviewMapper::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public ReviewDto addReview(@Valid ReviewDto reviewDto) throws NotFoundException {
        if (!storage.contains(reviewDto.getFilmId())) {
            log.warn("Не удалось найти фильма {}", reviewDto.getFilmId());
            throw new NotFoundException(String.format("Фильм \"%d\" не найден", reviewDto.getFilmId()));
        }

        if (!userStorage.contains(reviewDto.getUserId())) {
            log.warn("Не удалось найти пользователя {}", reviewDto.getUserId());
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", reviewDto.getUserId()));
        }

        var id = storage.addReview(reviewMapper.mapToReview(reviewDto));

        var result = reviewMapper.mapToReviewDto(storage.getReview(id));

        log.info(String.format("Успешно добавлен новый отзыв \"%s\"", result));

        return result;
    }

    public ReviewDto updateReview(ReviewDto reviewDto) throws NotFoundException {
        var id = reviewDto.getId();
        if (!storage.containsReview(id)) {
            log.warn("Не удалось найти отзыв {}", id);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", id));
        }

        storage.updateReview(reviewMapper.mapToReview(reviewDto));

        return reviewMapper.mapToReviewDto(storage.getReview(id));
    }

    public ReviewDto deleteReview(int id) throws NotFoundException {
        if (!storage.containsReview(id)) {
            log.warn("Не удалось найти отзыв {}", id);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", id));
        }

        var deletedReview = storage.getReview(id);

        storage.deleteReview(id);

        return reviewMapper.mapToReviewDto(deletedReview);
    }

    public void addLike(int reviewId, int userid, int useful) throws NotFoundException {
        if (!storage.containsReview(reviewId)) {
            log.warn("Не удалось найти отзыв {}", reviewId);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", reviewId));
        }

        if (!userStorage.contains(userid)) {
            log.warn("Не удалось найти пользователя {}", userid);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", userid));
        }

        storage.addReviewLike(reviewId, userid, useful);
    }

    public void deleteLike(int reviewId, int userid) throws NotFoundException {
        if (!storage.containsReview(reviewId)) {
            log.warn("Не удалось найти отзыв {}", reviewId);
            throw new NotFoundException(String.format("Отзыв \"%d\" не найден", reviewId));
        }

        if (!userStorage.contains(userid)) {
            log.warn("Не удалось найти пользователя {}", userid);
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден", userid));
        }

        if (!storage.containsReviewLike(reviewId, userid)) {
            log.warn(String.format("Не удалось найти лайк/дизлайк пользователя %d для отзыва %d", userid, reviewId));
            throw new NotFoundException(String.format("Пользователь \"%d\" не найден среди лайка/дизлайка для отзыва %d", userid, reviewId));
        }

        storage.deleteReviewLike(reviewId, userid);
    }
}
