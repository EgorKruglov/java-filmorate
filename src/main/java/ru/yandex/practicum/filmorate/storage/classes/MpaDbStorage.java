package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/* Класс для сохранения данных о пользователях приложения внутри базы данных H2 */
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(Integer mpaId) {
        String sqlQuery = "SELECT *\n" +
                "FROM mpa\n" +
                "WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, mpaId);
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Возрастное ограничение c id " + mpaId + " не найдено");
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT *\n" +
                "FROM mpa";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список возрастных ограничений");
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int i) {
        try {
            Mpa mpa = new Mpa();
            mpa.setId(resultSet.getInt("mpa_id"));
            mpa.setName(resultSet.getString("name"));
            return mpa;
        } catch (SQLException e) {
            throw new SQLErrorTransaction("Не удалось создать объект возрастного рейтинга фильма на основе бд");
        }
    }
}
