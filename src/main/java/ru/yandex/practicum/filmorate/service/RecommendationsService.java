package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationsService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper mapper;

    public Collection<FilmDto> getRecommendations(int userId) {
        Set<Film> mainUserLikedFilms = new HashSet<>(filmStorage.getUsersLikedFilms(userId));
        if (mainUserLikedFilms.isEmpty()) {
            return new HashSet<>();
        }
        Map<Integer, Set<Film>> usersFilms = findUsersLikedFilms();
        if (usersFilms.size() == 1) {
            return new HashSet<>();
        }
        usersFilms.remove(userId);

        Set<Integer> mainUserLikedFilmsIds = mainUserLikedFilms.stream().map(Film::getId).collect(Collectors.toSet());
        int id = findUserWithMaxMatches(mainUserLikedFilmsIds, usersFilms);
        if (id == -1) {
            return new HashSet<>();
        }
        return usersFilms.get(id).stream().filter(film -> !mainUserLikedFilmsIds.contains(film.getId())).map(mapper::mapToFilmDto).collect(Collectors.toSet());
    }

    private Map<Integer, Set<Film>> findUsersLikedFilms() {
        Map<Integer, Set<Film>> usersFilms = new HashMap<>();
        int id;
        Set<Film> likedFilms;
        for (User user : userStorage.getUsers()) {
            id = user.getId();
            likedFilms = new HashSet<>(filmStorage.getUsersLikedFilms(id));
            usersFilms.put(id, likedFilms);
        }
        return usersFilms;
    }

    private int findUserWithMaxMatches(Set<Integer> mainUserLikedFilmsIds, Map<Integer, Set<Film>> usersFilms) {
        int maxMatches = 0;
        int curMatches;
        int id = -1;
        for (Map.Entry<Integer, Set<Film>> entry : usersFilms.entrySet()) {
            curMatches = entry.getValue().stream().filter(film -> mainUserLikedFilmsIds.contains(film.getId())).toList().size();
            if (curMatches > maxMatches) {
                id = entry.getKey();
                maxMatches = curMatches;
            }
        }
        return id;
    }
}
