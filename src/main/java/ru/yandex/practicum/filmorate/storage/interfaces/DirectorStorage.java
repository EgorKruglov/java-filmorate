package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    List<Director> getDirectorsList();

    Director getDirectorById(Long directorId);

    List<Director> getDirectorByFilmId(Long filmId);

    boolean checkDirectorExistInDb(Long id);

    void addDirectorToFilm(Film film);

    void deleteDirectorsFromFilm(Long directorId);

    Director mapRow(ResultSet resultSet, int rowNum) throws SQLException;
}