package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

/* Класс для сохранения данных о фильмах приложения внутри базы данных H2 */
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
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
        directorStorage.addDirectorToFilm(film);
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
        directorStorage.deleteDirectorsFromFilm(Long.valueOf(film.getId()));
        directorStorage.addDirectorToFilm(film);
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
                directorStorage.getDirectorByFilmId(Long.valueOf(film.getId()));
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
        try {
            String sqlQuery = "DELETE\n" +
                    "FROM film_likes\n" +
                    "WHERE user_id = ?\n" +
                    "  AND film_id = ?\n" +
                    "LIMIT 1";
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DataAccessException e) {
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
                "FROM films f " +
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
            throw new SQLErrorTransaction("Не удалось отправить список фильмов");
        }
    }

    @Override
    public List<Film> getFilmRecommendations(Integer userId) {
        /*Запрос для получения id другого пользователя, у которого больше всего лайков на те фильмы, что у пользователя
        userId. И у которого просто больше всех лайков, чтобы было больше рекомендаций.*/
        String sqlQuery = "SELECT user_id\n" +
                "FROM (\n" +
                "    SELECT fl.user_id, COUNT(fl.film_id) AS like_count\n" +
                "    FROM film_likes fl\n" +
                "    WHERE fl.film_id IN (\n" +
                "        SELECT film_id\n" +
                "        FROM film_likes\n" +
                "        WHERE user_id = ?\n" +
                "    )\n" +
                "    AND fl.user_id <> ?\n" +
                "    GROUP BY fl.user_id\n" +
                "    ORDER BY like_count DESC\n" +
                "    LIMIT 1\n" +
                ") AS user_likes;";

        /*Запрос для получения фильмов найденного пользователя, исключая фильмы с лайками пользователя userId.*/
        String sqlQuery2 = "SELECT f.film_id, " +
                "f.name, " +
                "f.description, " +
                "f.release_date, " +
                "f.duration, " +
                "f.mpa_id, " +
                "FROM films f\n" +
                "JOIN film_likes fl ON f.film_id = fl.film_id AND fl.user_id = ?\n" +
                "LEFT JOIN film_likes fl2 ON f.film_id = fl2.film_id AND fl2.user_id = ?\n" +
                "WHERE fl2.user_id IS NULL;";

        Integer commonUserId;

        try {
            commonUserId = jdbcTemplate.queryForObject(sqlQuery, Integer.class, userId, userId);
        } catch (EmptyResultDataAccessException e) {  // Если не нашлось такого пользователя (нет лайков/слишком уникальные лайки)
            return List.of();
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список рекомендованных фильмов");
        }

        try {
            List<Film> films = jdbcTemplate.query(sqlQuery2, this::mapRowToFilm, commonUserId, userId);
            for (Film film : films) {
                fillFilmMpaNames(film);
                fillFilmGenres(film);
            }
            return films;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список рекомендованных фильмов");
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

    @Override
    public List<Film> getSortedDirectorFilms(Long directorId, String sortBy) {
        List<Film> foundedFilm;
        String sqlQuery;
        switch (sortBy) {
            case "year":
                try {
                    sqlQuery = "SELECT f.*, m.name FROM FILMS f " +
                            "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                            "LEFT JOIN director_films df ON f.film_id = df.film_id " +
                            "LEFT JOIN director d ON df.director_id = d.id " + " WHERE d.id = ? " +
                            "ORDER BY EXTRACT(YEAR FROM CAST(release_date AS date))";
                    foundedFilm = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
                    for (Film film : foundedFilm) {
                        fillFilmMpaNames(film);
                        fillFilmGenres(film);
                    }
                    return foundedFilm;
                } catch (DataAccessException e) {
                    e.printStackTrace();
                    throw new SQLErrorTransaction("Не удалось отправить список фильмов");
                }
            case "likes":
                sqlQuery = "SELECT f.*, r.name FROM FILMS f " +
                        "LEFT JOIN mpa r ON f.mpa_id = r.mpa_id " +
                        "LEFT JOIN director_films df ON f.film_id = df.film_id " +
                        "LEFT JOIN director d ON df.director_id = d.id " +
                        "LEFT JOIN film_likes fl ON f.film_id = fl.film_id " +
                        "WHERE d.id = ? GROUP BY f.film_id " +
                        "ORDER BY COUNT(fl.user_id) DESC";
                foundedFilm = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, directorId);
                for (Film film : foundedFilm) {
                    fillFilmMpaNames(film);
                    fillFilmGenres(film);
                }
                return foundedFilm;
            default:
                throw new Error("Unknown Sort Type");
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
                    new HashSet<>(),
                    directorStorage.getDirectorByFilmId(resultSet.getLong("film_id"))
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

    @Override
    public List<Film> searchFilmsByDirector(String director) {
        String sqlQuery = "SELECT f.*, m.name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN director_films df ON f.film_id = df.film_id " +
                "LEFT JOIN director d ON df.director_id = d.id " +
                "WHERE lower(d.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) DESC";
        List<Film> foundedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + director + "%");
        for (Film film : foundedFilms) {
            fillFilmMpaNames(film);
            fillFilmGenres(film);
        }
        return foundedFilms;
    }

    @Override
    public List<Film> searchFilmsByTitle(String title) {
        String sqlQuery = "SELECT f.*, m.name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "WHERE lower(f.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) DESC";
        List<Film> foundedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + title + "%");
        for (Film film : foundedFilms) {
            fillFilmMpaNames(film);
            fillFilmGenres(film);
        }
        return foundedFilms;
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        String sqlQuery = "SELECT f.*, m.name " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN director_films df ON f.film_id = df.film_id " +
                "LEFT JOIN director d ON df.director_id = d.id " +
                "WHERE lower(d.name) LIKE lower(?) OR lower(f.name) LIKE lower(?) " +
                "ORDER BY (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.film_id) DESC";
        List<Film> foundedFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, "%" + query + "%", "%" + query + "%");
        for (Film film : foundedFilms) {
            fillFilmMpaNames(film);
            fillFilmGenres(film);
        }
        return foundedFilms;
    }
}
