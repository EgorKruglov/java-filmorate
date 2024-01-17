package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final EventService eventService;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, EventService eventService, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.eventService = eventService;
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
        Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
        eventService.add(event);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Удаление лайка для фильма с Id {} от пользователя с Id {}", filmId, userId);
        filmStorage.deleteLike(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        eventService.add(event);
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

    public List<Film> getSortedDirectorFilms(Long directorId, String sortBy) {
        if (directorStorage.checkDirectorExistInDb(directorId)) {
            log.info("Получен список фильмов {} снятых режиссером с Id {}", sortBy, directorId);
            return filmStorage.getSortedDirectorFilms(directorId, sortBy);
        } else {
            throw new DirectorNotFoundException("Режиссёр не найден");
        }
    }

    public void deleteFilm(Integer filmId) {
        log.info("Удаление фильма id = {}", filmId);
        filmStorage.deleteFilm(filmId);
    }
}
