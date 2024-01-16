package ru.yandex.practicum.filmorate.otherFeaturesTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.classes.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRecommendationTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserDbStorage userStorage;
    private DirectorDbStorage directorStorage;
    User user1;
    Film film1;
    User user2;
    Film film2;

    @BeforeEach
    public void updateDb() {
        userStorage = new UserDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, directorStorage);
        user1 = userStorage.addUser(new User("user1@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990,
                1, 1)));
        film1 = filmStorage.addFilm(new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10),
                90, new Mpa(1, null), Set.of(new Genre(1, null), new Genre(2, null)), List.of()));
        user2 = userStorage.addUser(new User("user2@email.ru", "Petya321", "Petya Ivanov", LocalDate.of(1991,
                1, 1)));
        film2 = filmStorage.addFilm(new Film(2, "Film Two", "description2", LocalDate.of(2011, 5, 10),
                100, new Mpa(2, null), Set.of(new Genre(3, null)), List.of()));
    }

    @Test
    public void testFilmRecommendationNoLikes() {
        Director director = new Director(1L, "First director");
        Director createDirector = directorStorage.createDirector(director);
        List<Film> recommendations = filmStorage.getFilmRecommendations(user1.getId());
        assertThat(recommendations).isEmpty();
    }

    @Test
    public void testFilmRecommendationNoSameLikes() {
        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user2.getId());
        List<Film> recommendations = filmStorage.getFilmRecommendations(user2.getId());
        assertThat(recommendations).isEmpty();
    }

    @Test
    public void testFilmRecommendationSameLikes() {
        filmStorage.addLike(film1.getId(), user1.getId());
        filmStorage.addLike(film2.getId(), user1.getId());
        filmStorage.addLike(film1.getId(), user2.getId());
        List<Film> recommendations = filmStorage.getFilmRecommendations(user2.getId());
        assertThat(recommendations)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film2));

        filmStorage.deleteLike(film2.getId(), user1.getId());
        recommendations = filmStorage.getFilmRecommendations(user2.getId());
        assertThat(recommendations).isEmpty();
    }
}
