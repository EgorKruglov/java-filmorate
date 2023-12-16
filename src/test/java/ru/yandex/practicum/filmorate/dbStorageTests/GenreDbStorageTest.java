package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.classes.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    @BeforeEach
    public void updateDb() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void testGetGenreById() {
        Genre resultGenre = genreStorage.getGenreById(3);

        assertThat(resultGenre).isNotNull();
        assertThat(resultGenre).isEqualTo(new Genre(3, "Мультфильм"));
    }

    @Test
    public void testGetGenres() {
        List<Genre> genres = genreStorage.getGenres();

        assertThat(genres).isNotNull();
        assertThat(genres.get(0)).isEqualTo(new Genre(1, "Комедия"));
        assertThat(genres.get(4)).isEqualTo(new Genre(5, "Документальный"));
    }
}
