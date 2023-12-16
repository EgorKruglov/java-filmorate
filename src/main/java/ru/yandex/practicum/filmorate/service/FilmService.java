package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.List;

@Service
@Qualifier("filmDbStorage")
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer filmId) {
        if (filmId < 0) {
            throw new FilmNotFoundException("Id фильма должен быть неотрицательным");
        }
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }
}
