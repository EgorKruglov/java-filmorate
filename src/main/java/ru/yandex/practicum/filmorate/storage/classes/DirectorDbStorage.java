package ru.yandex.practicum.filmorate.storage.classes;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_QUERY_DIRECTOR_FROM_DB = "SELECT * FROM director";

    @Override
    public Director createDirector(Director director) {
        // Создать режиссёра
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("id");
        try {
            director.setId(simpleJdbcInsert.executeAndReturnKey(Map.of("name", director.getName())).longValue());
            return director;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить режиссёра");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        // Обновить данные о режиссёре
        String sqlQuery = "UPDATE director " +
                "SET  name = ? " +
                "WHERE id = ?;";
        try {
            jdbcTemplate.update(sqlQuery,
                    director.getName(),
                    director.getId());
            return director;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось обновить режиссера");
        }
    }

    @Override
    public void deleteDirector(Long directorId) {
        // Удалить данные о режиссёре
        String sqlQuery = "DELETE FROM director " +
                "WHERE id=?";
        try {
            jdbcTemplate.update(sqlQuery, String.valueOf(directorId));
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось удалить данные режиссера id:" + directorId);
        }
    }

    @Override
    public List<Director> getDirectorsList() {
        // Получить список режиссёров
        List<Director> directors;
        try {
            return jdbcTemplate.query(SQL_QUERY_DIRECTOR_FROM_DB, this::mapRow);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список режиссёров");
        }
    }

    @Override
    public Director getDirectorById(Long directorId) {
        // Получить данные о режиссёре
        String sqlQuery = SQL_QUERY_DIRECTOR_FROM_DB + " WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRow, directorId);
        } catch (DataAccessException e) {
            throw new DirectorNotFoundException("Режиссёр c id " + directorId + " не найден");
        }
    }

    @Override
    public List<Director> getDirectorByFilmId(Long filmId) {
        // Получить режиссёра по ID фильма
        String sqlQuery = "SELECT d.id, d.name " +
                "FROM director_films AS df " +
                "LEFT JOIN director AS d ON df.director_id=d.id " +
                "WHERE film_id = ?";
        try {
            if (jdbcTemplate.queryForList(sqlQuery, filmId).isEmpty()) {
                return List.of();
            }
            return jdbcTemplate.query(sqlQuery, this::mapRow, filmId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось получить список режиссёров по фильму с id:" + filmId);
        }
    }

    @Override
    public boolean checkDirectorExistInDb(Long id) {
        // Узнать есть ли данный режиссёр в базе
        String sqlQuery = SQL_QUERY_DIRECTOR_FROM_DB + " WHERE id = ?";
        try {
            return !jdbcTemplate.query(sqlQuery, this::mapRow, id).isEmpty();
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось найти режиссёра с id:" + id);
        }
    }

    @Override
    public void addDirectorToFilm(Film film) {
        // Добавить режиссёра к фильму
        String sqlQuery = "INSERT into director_films (film_id, director_id) values(?, ?);";
        if (film.getDirectors() != null) {
            try {
                if (!film.getDirectors().isEmpty()) {
                    for (Director director : film.getDirectors()) {
                        try {
                            jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
                        } catch (DataAccessException e) {
                            throw new SQLErrorTransaction("Не удалось добавить режиссёра в фильм:" + film);
                        }
                    }
                }
            } catch (DataAccessException e) {
                throw new SQLErrorTransaction("Режиссёр уже добавлен в фильм:" + film);
            }
        }
    }

    @Override
    public void deleteDirectorsFromFilm(Long filmId) {
        // Удалить данные о режиссёре из фильма по ID фильма
        String sqlQuery = "DELETE FROM director_films " +
                "WHERE film_id = ?";
        try {
            jdbcTemplate.update(sqlQuery, String.valueOf(filmId));
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось удалить данные режиссёра из фильма с id:" + filmId);
        }
    }

    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        try {
            return Director.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("Name"))
                    .build();
        } catch (SQLException e) {
            throw new SQLErrorTransaction("Не удалось создать объект режиссёра на основе базы данных");
        }
    }
}