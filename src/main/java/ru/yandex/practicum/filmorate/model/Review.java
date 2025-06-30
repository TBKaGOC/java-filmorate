package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"reviewId"})
public class Review {
    private Integer reviewId;
    @NotBlank private String content;
    @NotNull private Boolean isPositive;
    @NotNull private Integer filmId;
    @NotNull private Integer userId;
    @NotBlank private Integer useful;
}
