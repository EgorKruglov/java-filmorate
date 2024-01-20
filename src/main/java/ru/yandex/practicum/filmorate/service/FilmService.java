package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.UnknownSearchingParameterException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, DirectorStorage directorStorage, EventStorage eventStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.eventStorage = eventStorage;
        this.genreDbStorage = genreDbStorage;
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
        eventStorage.add(event);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (filmId < 0 || userId < 0) {
            throw new FilmNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Удаление лайка для фильма с Id {} от пользователя с Id {}", filmId, userId);
        filmStorage.deleteLike(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        eventStorage.add(event);
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

    public List<Film> searchFilms(String query, String by) {
        if (by.equals("director")) {
            return filmStorage.searchFilmsByDirector(query);
        } else if (by.equals("title")) {
            return filmStorage.searchFilmsByTitle(query);
        } else if (by.equals("title,director") || by.equals("director,title")) {
            return filmStorage.searchFilmsByDirectorAndTitle(query);
        } else {
            throw new UnknownSearchingParameterException("Неверный пареметр поиска:" + by);
        }
    }

    public void deleteFilm(Integer filmId) {
        log.info("Удаление фильма id = {}", filmId);
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        Collection<Film> listOfUserFilms = filmStorage.getFilmsByUser(userId);
        Collection<Film> listOfFriendFilms = filmStorage.getFilmsByUser(friendId);
        Set<Film> commonList = new HashSet<>(listOfUserFilms);
        commonList.retainAll(listOfFriendFilms);
        return new ArrayList<>(commonList);
    }

    public List<Film> getPopularFilms(int count, Optional<Integer> genreId, Optional<Integer> year) {
        Map<Film, Integer> filmsMap = new HashMap<>();
        for (Film f: filmStorage.getFilms()) {
            filmsMap.put(f, filmStorage.getLikesCount(f.getId()));
        }
        if (genreId.isEmpty() && year.isEmpty()) {
            log.info("Запрос популярных фильмов с параметром - колличество {}.", count);
            return getFilms().stream()
                    .sorted(this::compare)
                    .limit(count)
                    .collect(Collectors.toList());
        } else if (year.isEmpty()) {
            log.info("Запрос популярных фильмов с параметрами: колличество {}, жанр  {}", count, genreId.get());
            genreDbStorage.getGenreById(genreId.get());
            return filmsMap.entrySet().stream()
                    .sorted((Comparator.comparingInt(Map.Entry::getValue)))
                    .filter(e -> e.getKey().getGenres().stream().anyMatch(g -> g.getId() == genreId.get()))
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .sorted((Comparator.comparing(Film::getId)))
                    .collect(Collectors.toList());
        } else if (genreId.isEmpty()) {
            log.info("Запрос популярных фильмов с параметрами: колличество {}, год  {}", count, year.get());
            return filmsMap.entrySet().stream()
                    .sorted((Comparator.comparingInt(Map.Entry::getValue)))
                    .filter(e -> e.getKey().getReleaseDate().getYear() == year.get())
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .sorted((Comparator.comparing(Film::getId)))
                    .collect(Collectors.toList());
        } else {
            log.info("Запрос популярных фильмов с параметрами: колличество {}, жанр  {}, год  {}",
                    count, genreId.get(), year.get());
            genreDbStorage.getGenreById(genreId.get());
            return filmsMap.entrySet().stream()
                    .sorted((Comparator.comparingInt(Map.Entry::getValue)))
                    .filter(e -> e.getKey().getGenres().stream().anyMatch(g -> g.getId() == genreId.get()))
                    .filter(e -> e.getKey().getReleaseDate().getYear() == year.get())
                    .map(Map.Entry::getKey)
                    .limit(count)
                    .sorted((Comparator.comparing(Film::getId)))
                    .collect(Collectors.toList());
        }
    }

    private int compare(Film film, Film otherFilm) {
        return Integer.compare(filmStorage.getLikesCount(otherFilm.getId()), filmStorage.getLikesCount(film.getId()));
    }
}
