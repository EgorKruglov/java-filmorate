package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.classes.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;

    @BeforeEach
    public void updateDb() {
        mpaStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    public void testGetMpaById() {
        Mpa resultMpa = mpaStorage.getMpaById(3);

        assertThat(resultMpa).isNotNull();
        assertThat(resultMpa).isEqualTo(new Mpa(3, "PG-13"));
    }

    @Test
    public void testGetGenres() {
        List<Mpa> allMpa = mpaStorage.getAllMpa();

        assertThat(allMpa).isNotNull();
        assertThat(allMpa.get(0)).isEqualTo(new Mpa(1, "G"));
        assertThat(allMpa.get(4)).isEqualTo(new Mpa(5, "NC-17"));
    }
}