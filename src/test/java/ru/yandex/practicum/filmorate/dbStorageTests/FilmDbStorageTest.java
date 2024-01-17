package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmStorage;
    private UserDbStorage userStorage;
    private DirectorStorage directorStorage;

    @BeforeEach
    public void updateDb() {
        filmStorage = new FilmDbStorage(jdbcTemplate, new UserDbStorage(jdbcTemplate), new DirectorDbStorage(jdbcTemplate));
        userStorage = new UserDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddFilmNoMpaNoGenre() {
        Film film = new Film("Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null));
        Film addedFilm = filmStorage.addFilm(film);

        assertThat(addedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void testAddFilmWithMpaAndGenre() {
        Film film = new Film("Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(new Genre(1, null), new Genre(2, null)));
        Film addedFilm = filmStorage.addFilm(film);

        assertThat(addedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film("Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null));
        Film addedFilm = filmStorage.addFilm(film);

        addedFilm.setGenres(Set.of(new Genre(2, null), new Genre(4, null)));
        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedFilm);
    }

    @Test
    public void testGetFilms() {
        Film film1 = new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(), List.of());
        Film film2 = new Film(2, "Film Two", "description2", LocalDate.of(2010, 5, 10), 90, new Mpa(2, null), Set.of(new Genre(1, null), new Genre(2, null)), List.of());
        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);
        List<Film> films = filmStorage.getFilms();
        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(addedFilm1, addedFilm2));
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(), List.of());
        Film addedFilm = filmStorage.addFilm(film);

        Film retrievedFilm = filmStorage.getFilmById(addedFilm.getId());

        assertThat(retrievedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedFilm);
    }

    @Test
    public void testAddLike() {
        Film film1 = new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(), List.of());
        Film film2 = new Film(2, "Film Two", "description2", LocalDate.of(2019, 5, 10), 90, new Mpa(2, null), Set.of(new Genre(1, null), new Genre(2, null)), List.of());
        Film film3 = new Film(3, "Film Three", "description3", LocalDate.of(2011, 5, 10), 90, new Mpa(3, null), Set.of(), List.of());
        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);
        Film addedFilm3 = filmStorage.addFilm(film3);

        User user = userStorage.addUser(new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1)));

        filmStorage.addLike(addedFilm1.getId(), user.getId());
        filmStorage.addLike(addedFilm2.getId(), user.getId());
        filmStorage.addLike(addedFilm2.getId(), user.getId());
        List<Film> topFilms = filmStorage.getTopFilms(10);

        assertThat(topFilms).hasSize(3);

        assertThat(topFilms.get(0)).isEqualTo(addedFilm2);  // Проверка порядка фильмов в списке
        assertThat(topFilms.get(1)).isEqualTo(addedFilm1);
        assertThat(topFilms.get(2)).isEqualTo(addedFilm3);
    }

    @Test
    public void testDeleteLike() {
        Film film1 = new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(), List.of());
        Film film2 = new Film(2, "Film Two", "description2", LocalDate.of(2019, 5, 10), 90, new Mpa(2, null), Set.of(new Genre(1, null), new Genre(2, null)), List.of());
        Film film3 = new Film(3, "Film Three", "description3", LocalDate.of(2011, 5, 10), 90, new Mpa(3, null), Set.of(), List.of());
        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);
        Film addedFilm3 = filmStorage.addFilm(film3);

        User user = userStorage.addUser(new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1)));

        filmStorage.addLike(addedFilm1.getId(), user.getId());
        filmStorage.addLike(addedFilm1.getId(), user.getId());
        filmStorage.addLike(addedFilm2.getId(), user.getId());
        filmStorage.addLike(addedFilm2.getId(), user.getId());
        filmStorage.addLike(addedFilm3.getId(), user.getId());

        filmStorage.deleteLike(addedFilm2.getId(), user.getId());
        filmStorage.deleteLike(addedFilm2.getId(), user.getId());
        List<Film> topFilms = filmStorage.getTopFilms(10);

        assertThat(topFilms).hasSize(3);

        assertThat(topFilms.get(0)).isEqualTo(addedFilm1);  // Проверка порядка фильмов в списке
        assertThat(topFilms.get(1)).isEqualTo(addedFilm3);
        assertThat(topFilms.get(2)).isEqualTo(addedFilm2);
    }

    @Test
    public void testGetTopFilms() {
        Film film1 = new Film(1, "Film One", "description1", LocalDate.of(2010, 5, 10), 90, new Mpa(1, null), Set.of(), List.of());
        Film film2 = new Film(2, "Film Two", "description2", LocalDate.of(2019, 5, 10), 90, new Mpa(2, null), Set.of(new Genre(1, null), new Genre(2, null)), List.of());

        Film addedFilm1 = filmStorage.addFilm(film1);
        Film addedFilm2 = filmStorage.addFilm(film2);

        User user = userStorage.addUser(new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1)));

        filmStorage.addLike(addedFilm2.getId(), user.getId());

        List<Film> topFilms = filmStorage.getTopFilms(10);
        assertThat(topFilms).hasSize(2);
        assertThat(topFilms.get(0)).isEqualTo(addedFilm2);
        assertThat(topFilms.get(1)).isEqualTo(addedFilm1);

        Film film3 = new Film(3, "Film Three", "description3", LocalDate.of(2011, 5, 10), 90, new Mpa(3, null), Set.of(), List.of());
        Film addedFilm3 = filmStorage.addFilm(film3);

        filmStorage.addLike(addedFilm3.getId(), user.getId());
        filmStorage.addLike(addedFilm3.getId(), user.getId());

        topFilms = filmStorage.getTopFilms(3);

        assertThat(topFilms).hasSize(3);

        assertThat(topFilms.get(0)).isEqualTo(addedFilm3);  // Проверка порядка фильмов в списке
        assertThat(topFilms.get(1)).isEqualTo(addedFilm2);
        assertThat(topFilms.get(2)).isEqualTo(addedFilm1);
    }
}
