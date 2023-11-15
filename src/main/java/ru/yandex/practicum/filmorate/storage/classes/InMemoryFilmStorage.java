package ru.yandex.practicum.filmorate.storage.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage {

    private final Map<Integer, Film> films;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        userStorage = inMemoryUserStorage;
        films = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            log.warn("Фильм с id " + film.getId() + " уже существует");
            throw new FilmAlreadyExistException("Фильм с id " + film.getId() + " уже существует");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id " + film.getId() + " не найден");
            throw new FilmNotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return films;
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Фильм с id " + filmId + " не найден");
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }
        return films.get(filmId);
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);  // Проверка объектов в базе данных
        this.getFilmById(filmId);
        films.get(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);  // Проверка объектов в базе данных
        this.getFilmById(filmId);
        films.get(filmId).deleteLike(userId);
    }
}
