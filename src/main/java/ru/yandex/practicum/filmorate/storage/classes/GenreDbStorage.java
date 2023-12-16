package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        String sqlQuery = "SELECT *\n" +
                "FROM genres\n" +
                "WHERE genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, genreId);
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Жанр фильма c id " + genreId + " не найден");
        }
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT *\n" +
                "FROM genres";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список всех жанров фильмов");
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int i) {
        try {
            Genre genre = new Genre();
            genre.setId(resultSet.getInt("genre_id"));
            genre.setName(resultSet.getString("name"));
            return genre;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось создать объект жанра фильма на основе бд");
        }
    }
}
