package ru.yandex.practicum.filmorate.storage.classes;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
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

    @Override
    public Director createDirector(Director director) {
        // Создать режисера
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(Map.of("name", director.getName())).longValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        // Обновить данные о режисере
        String sqlQuery = "UPDATE director " +
                "SET  name = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        // Удалить данные о режисере
        String sqlQuery = "DELETE FROM director " +
                "WHERE id=?";
        jdbcTemplate.update(sqlQuery, String.valueOf(directorId));
    }

    @Override
    public List<Director> getDirectorsList() {
        List<Director> directors;
        // Получить список режисеров
        String sqlQuery = "SELECT * " +
                "FROM director";
        directors = jdbcTemplate.query(sqlQuery, this::mapRow);
        return directors;
    }

    @Override
    public Director getDirectorById(Long directorId) {
        // Получить данные о режисере
        String sqlQuery = "SELECT * " +
                "FROM director " +
                "WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRow, directorId);
    }

    @Override
    public List<Director> getDirectorByFilmId(Long filmId) {
        // Получить режисера по ID фильма
        String sqlQuery = "SELECT d.id, d.name " +
                "FROM director_films AS df " +
                "LEFT JOIN director AS d ON df.director_id=d.id " +
                "WHERE film_id = ?";
        if (jdbcTemplate.queryForList(sqlQuery, filmId).isEmpty()) {
            return List.of();
        }
        return jdbcTemplate.query(sqlQuery, this::mapRow, filmId);
    }

    @Override
    public boolean checkDirectorExistInDb(Long id) {
        // Узнать есть ли данный режисер в базе
        String sqlQuery = "SELECT * " +
                "FROM director " +
                "WHERE id = ?";
        return !jdbcTemplate.query(sqlQuery, this::mapRow, id).isEmpty();
    }

    @Override
    public void addDirectorToFilm(Film film) {
        // Добавить режисера к фильму
        String sqlQuery = "INSERT into director_films (film_id, director_id) values(?, ?);";
        if (film.getDirectors() != null) {
            if (!film.getDirectors().isEmpty()) {
                for (Director director : film.getDirectors()) {
                    jdbcTemplate.update(sqlQuery, film.getId(), director.getId());
                }
            }
        }
    }

    @Override
    public void deleteDirectorsFromFilm(Long filmId) {
        // Удалить данные о режисере по ID фильма
        String sqlQuery = "DELETE FROM director_films " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, String.valueOf(filmId));
    }

    @Override
    public Director mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("Name"))
                .build();
    }
}