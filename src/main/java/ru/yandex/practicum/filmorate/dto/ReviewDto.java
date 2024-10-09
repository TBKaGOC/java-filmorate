package ru.yandex.practicum.filmorate.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReviewDto {
    int id;

    @NotBlank
    String content;

    @NotNull
    ReviewType reviewType;

    int filmId;

    int userId;

    int useful;
}

