package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(of = {"id"})
public class ReviewLike {
    @NotNull Integer id;
    @NotNull Integer reviewId;
    @NotNull Integer userId;
    @NotNull Integer useful;
}
