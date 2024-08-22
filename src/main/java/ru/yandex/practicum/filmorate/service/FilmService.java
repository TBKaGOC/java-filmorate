package ru.yandex.practicum.filmorate.service;

import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Service
@Setter
public class FilmService {
    private int sizeOfTop = 10;

    public void addLike(Integer likedUser, Film film) {
        film.addLike(likedUser);
    }

    public void deleteLike(Integer unlikedUser, Film film) {
        film.deleteLike(unlikedUser);
    }

    public List<Film> getMostPopular(FilmStorage storage) {
        List<Film> resultList = storage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesNumber).reversed())
                .toList();
        if (sizeOfTop >= resultList.size()) {
            return resultList;
        } else {
            return resultList.subList(0, sizeOfTop);
        }
    }
}
