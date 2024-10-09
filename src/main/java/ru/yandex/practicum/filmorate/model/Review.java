package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"reviewId"})
public class Review {
    Integer reviewId;
    @NotBlank String content;
    @NotBlank String reviewType;
    @NotNull Integer filmId;
    @NotNull Integer userId;
    @NotBlank Integer useful;
}
