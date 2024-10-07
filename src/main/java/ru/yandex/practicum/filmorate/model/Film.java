package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
public class Film {
    Integer id;
    @NotBlank String name;
    @Size(max = 200) String description;
    @Past LocalDate releaseDate;
    @Positive Integer duration;
    private Set<Integer> likedUsers;
    private Rating rating;
    private Set<Genre> genres;
    public static final LocalDate EARLY_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    public void addLike(Integer id) {
        if (likedUsers == null) {
            likedUsers = new HashSet<>();
        }

        likedUsers.add(id);
    }

    public void deleteLike(Integer id) {
        if (likedUsers != null) {
            likedUsers.remove(id);
        }
    }

    public int getLikesNumber() {
        if (likedUsers != null) {
            return likedUsers.size();
        } else {
            return 0;
        }
    }
}
