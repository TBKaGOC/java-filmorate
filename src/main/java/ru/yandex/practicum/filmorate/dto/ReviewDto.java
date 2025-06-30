package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReviewDto {
    int reviewId;

    @NotBlank
    private String content;

    @NotNull
    private Boolean isPositive;

    private Integer filmId;

    private Integer userId;

    private int useful;
}

