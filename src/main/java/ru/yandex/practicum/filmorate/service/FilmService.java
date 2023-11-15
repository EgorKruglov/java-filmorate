package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.modelsForRequest.RequestFilm;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.util.IdGeneratorFilms;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public RequestFilm addFilm(RequestFilm requestFilm) {
        requestFilm.setId(IdGeneratorFilms.generate());
        filmStorage.addFilm(convertRequestFilmToFilm(requestFilm));
        return requestFilm;
    }

    public RequestFilm updateFilm(RequestFilm requestFilm) {
        Film film =
        filmStorage.updateFilm(convertRequestFilmToFilm(requestFilm));
        return requestFilm;
    }

    public List<RequestFilm> getFilms() {
        Map<Integer, Film> allFilms = filmStorage.getFilms();
        ArrayList<RequestFilm> requestFilms = new ArrayList<>(allFilms.size());
        for (Film film : allFilms.values()) {
            requestFilms.add(convertFilmToRequestFilm(film));
        }
        return requestFilms;
    }

    public RequestFilm getFilmById(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId);
        return convertFilmToRequestFilm(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<RequestFilm> getTopFilms(int count) {
        Map<Integer, Film> allFilms = filmStorage.getFilms();
        if (allFilms.size() < count) {  // Сверяем с количеством фильмов в базе
            count = allFilms.size();
        }

        List<Film> topFilms = allFilms.values().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersIdsWhoLiked().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());

        List<RequestFilm> requestFilms = new ArrayList<>(count);
        for (Film film : topFilms) {
            requestFilms.add(convertFilmToRequestFilm(film));
        }
        return requestFilms;
    }

    private RequestFilm convertFilmToRequestFilm(Film film) {
        RequestFilm requestFilm = new RequestFilm(film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toMinutes());
        requestFilm.setId(film.getId());
        return requestFilm;
    }

    private Film convertRequestFilmToFilm(RequestFilm requestFilm) {
        return new Film(requestFilm.getId(),  // Создаём настоящий объект фильма из класса-обёртки
                requestFilm.getName(),
                requestFilm.getDescription(),
                requestFilm.getReleaseDate(),
                Duration.ofMinutes(requestFilm.getDuration()));
    }
}
