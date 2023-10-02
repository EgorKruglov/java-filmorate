package ru.yandex.practicum.filmorate.validationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmValidationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void addFilmSuccess() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.OK;
    }

    @Test
    public void addFilmBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Название фильма не может быть пустым");
    }

    @Test
    public void addFilmOverDescription() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A very long description that exceeds the maximum allowed length of 200 characters. A very " +
                "long description that exceeds the maximum allowed length of 200 characters. eeeeeeeeeeeeeeeeeeeeeeeeee" +
                "eeeeeeeeeeeeeeeeeeeeeeeee");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Описание должно быть меньше 200 символов");
    }

    @Test
    public void addFilmAncientReleaseDAte() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Дата выпуска фильма должна быть после 28.12.1895 г.");
    }

    @Test
    public void addFilmNegativeDuration() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(Duration.ofMinutes(-30));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Продолжительность фильма должна быть положительным числом");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/films";
    }
}
