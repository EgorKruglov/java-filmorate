package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        log.info("Добавление фильма: {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма: {}", film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        log.info("Получение списка фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer filmId) {
        if (filmId < 0) {
            throw new FilmNotFoundException("Id фильма должен быть неотрицательным");
        }
        log.info("Получение фильма по Id: {}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Добавление лайка для фильма с Id {} от пользователя с Id {}", filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Удаление лайка для фильма с Id {} от пользователя с Id {}", filmId, userId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        log.info("Получение топ {} фильмов", count);
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getFilmRecommendations(Integer userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Получение списка рекомендованных фильмов для пользователя с id:{}", userId);
        return filmStorage.getFilmRecommendations(userId);
    }
}
