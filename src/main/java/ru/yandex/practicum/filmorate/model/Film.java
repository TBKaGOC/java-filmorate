package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
public class Film {
    @EqualsAndHashCode.Include Integer id;
    @NotBlank String name;
    @Size(max = 200) String description;
    @Past LocalDate releaseDate;
    @Positive Integer duration;
    public static final LocalDate EARLY_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
}
