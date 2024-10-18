package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class ReviewLike {
    @NotNull private Integer id;
    @NotNull private Integer reviewId;
    @NotNull private Integer userId;
    @NotNull private Integer useful;
}
