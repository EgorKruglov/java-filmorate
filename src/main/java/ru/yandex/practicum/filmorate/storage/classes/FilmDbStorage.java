package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

/* Класс для сохранения данных о фильмах приложения внутри базы данных H2 */
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    @Override
    public Film addFilm(Film film) {
        try {  // Добавить фильм
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            int filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
            film.setId(filmId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить фильм");
        }

        try {  // Добавить записи о жанрах фильма
            if (film.getGenres() != null) {
                String sqlQuery = "INSERT INTO film_genres(film_id, genre_id)\n" +
                        "VALUES (?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
                }
            }
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить связь фильма и его жанра");
        }

        fillFilmMpaNames(film);
        fillFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());  // Проверка наличия фильма в бд

        try {  // Обновить фильм
            String sqlQuery = "UPDATE films\n" +
                    "SET name = ?,\n" +
                    "    description = ?,\n" +
                    "    release_date = ?,\n" +
                    "    duration = ?,\n" +
                    "    mpa_id = ?\n" +
                    "WHERE film_id = ?";

            jdbcTemplate.update(sqlQuery,
            film.getName(),
            film.getDescription(),
            film.getReleaseDate(),
            film.getDuration(),
            film.getMpa().getId(),
            film.getId());
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось обновить фильм");
        }

        try {  // Удалить старые записи жанров фильма
            String sqlQuery = "DELETE\n" +
                    "FROM film_genres\n" +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId());
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось удалить старые жанры фильма");
        }

        try {  // Добавить записи жанров фильма
            if (film.getGenres() != null) {
                String sqlQuery = "INSERT INTO film_genres(film_id, genre_id)\n" +
                        "VALUES (?, ?)";
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
                }
            }
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить связь фильма и его жанра");
        }

        fillFilmMpaNames(film);
        fillFilmGenres(film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT *\n" +
                "FROM films";
        try {
            List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
            for (Film film : films) {
                fillFilmMpaNames(film);
                fillFilmGenres(film);
            }
            return films;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список фильмов");
        }
    }

    @Override
    public Film getFilmById(int filmId) {
        String sqlQuery = "SELECT *\n" +
                "FROM films\n" +
                "WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
            fillFilmMpaNames(film);
            fillFilmGenres(film);
            return film;
        } catch (DataAccessException e) {
            throw new FilmNotFoundException("Фильм c id " + filmId + " не найден");
        }
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        try {
            String sqlQuery = "INSERT INTO film_likes(user_id, film_id)\n" +
                    "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить лайк на фильм id:" + filmId);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        try {
            String sqlQuery = "DELETE\n" +
                    "FROM film_likes\n" +
                    "WHERE user_id = ?\n" +
                    "  AND film_id = ?\n" +
                    "LIMIT 1";
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось удалить лайк с фильма id:" + filmId);
        }
    }


    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "COUNT(fl.user_id) AS likes_count " +
                "FROM films f " + // Исправлено на правильное имя таблицы
                "LEFT JOIN film_likes fl ON fl.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC NULLS LAST, f.release_date DESC " +
                "LIMIT ?;";
        try {
            List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
            for (Film film : films) {
                fillFilmMpaNames(film);
                fillFilmGenres(film);
            }
            return films;
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось отправить список фильмов");
        }
    }

    private void fillFilmGenres(Film film) {
        String sqlQuery = "SELECT * " +
                "FROM genres AS g " +
                "JOIN film_genres AS fg ON g.genre_id = fg.genre_id\n" +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id ASC";
        try {
            List<Genre> genreList = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, film.getId());
            film.setGenres(new HashSet<>());  // Удалить записи о жанрах без имени
            for (Genre genre : genreList) {
                film.getGenres().add(genre);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось получить список жанров фильма");
        }
    }

    private void fillFilmMpaNames(Film film) {
        if (film.getMpa() != null) {  // Заполнить имя жанра
            if (film.getMpa().getName() == null) {
                String sqlQuery = "SELECT *\n" +
                        "FROM mpa\n" +
                        "WHERE mpa_id = ?";
                try {
                    Mpa mpa = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, film.getMpa().getId());
                    film.setMpa(mpa);
                } catch (DataAccessException e) {
                    throw new SQLErrorTransaction("Не удалось создать объект возрастного рейтинга фильма на основе бд");
                }
            }
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) {
        try {
            return new Film(resultSet.getInt("film_id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getDate("release_date").toLocalDate(),
                    resultSet.getInt("duration"),
                    new Mpa(resultSet.getInt("mpa_id"), null),
                    new HashSet<>()
                    );
        } catch (SQLException e) {
            throw new SQLErrorTransaction("Не удалось создать объект фильма на основе бд");
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
