package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public List<Film> getFilms();

    public Film getFilmById(int filmId);

    List<Film> getSortedDirectorFilms(Long directorId, String sortBy);

    public void addLike(Integer filmId, Integer userId);

    public void deleteLike(Integer filmId, Integer userId);

    public List<Film> getTopFilms(int count);

    List<Film> searchFilmsByDirector(String director);

    List<Film> searchFilmsByTitle(String title);

    List<Film> searchFilmsByDirectorAndTitle(String query);

    List<Film> getFilmRecommendations(Integer userId);

    void deleteFilm(Integer filmId);

    Collection<Film> getFilmsByUser(Integer id);

    Integer getLikesCount(Integer filmId);
}
