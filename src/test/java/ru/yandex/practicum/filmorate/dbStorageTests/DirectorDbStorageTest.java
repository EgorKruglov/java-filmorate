package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.classes.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private DirectorStorage directorStorage;

    @BeforeEach
    public void updateDb() {
        directorStorage = new DirectorDbStorage(jdbcTemplate);
    }

    @Test
    public void testCreateDirector() {
        Director director = new Director(1L, "First director");
        Director createDirector = directorStorage.createDirector(director);

        assertThat(createDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    public void testUpdateDirector() {
        Director director = new Director(1L, "First director");
        Director createDirector = directorStorage.createDirector(director);

        createDirector.setId(2L);
        createDirector.setName("Second director");

        Director updateDirector = directorStorage.updateDirector(createDirector);

        assertThat(updateDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director);
    }

    @Test
    public void testDeleteDirector() {
        Director director1 = new Director(1L, "First director");
        Director director2 = new Director(2L, "First director");
        Director createDirector1 = directorStorage.createDirector(director1);
        Director createDirector2 = directorStorage.createDirector(director2);

        directorStorage.deleteDirector(createDirector1.getId());
        directorStorage.deleteDirector(createDirector2.getId());

        assertThat(createDirector1)
                .isNotNull();
        assertThat(createDirector2)
                .isNotNull();
    }


    @Test
    public void testGetDirectorList() {
        Director director1 = new Director(1L, "First director");
        Director director2 = new Director(2L, "First director");
        Director createDirector1 = directorStorage.createDirector(director1);
        Director createDirector2 = directorStorage.createDirector(director2);

        List<Director> directorsList = directorStorage.getDirectorsList();

        assertThat(directorsList.get(0)).isEqualTo(createDirector1);
        assertThat(directorsList.get(1)).isEqualTo(createDirector2);
    }

    @Test
    public void testGetDirectorById() {
        Director director1 = new Director(1L, "First director");
        Director createDirector1 = directorStorage.createDirector(director1);

        directorStorage.deleteDirector(createDirector1.getId());

        assertThat(createDirector1)
                .isNotNull();
    }
}
