package ru.yandex.practicum.filmorate.validationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.modelsForRequest.RequestFilm;

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
        RequestFilm film = new RequestFilm(
                "Valid name",
                "Valid description",
                LocalDate.of(2022, 1, 1),
                120);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        System.out.println(response);

        assert response.getStatusCode() == HttpStatus.OK;
    }

    @Test
    public void addFilmBlankName() {
        RequestFilm film = new RequestFilm(
                "",
                "Valid description",
                LocalDate.of(2022, 1, 1),
                120);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Название фильма не может быть пустым");
    }

    @Test
    public void addFilmOverDescription() {
        RequestFilm film = new RequestFilm(
                "Valid Film",
                "A very long description that exceeds the maximum allowed length of 200 characters. A very " +
                        "long description that exceeds the maximum allowed length of 200 characters. eeeeeeeeeeeeeeeeeee" +
                        "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
                LocalDate.of(2022, 1, 1),
                120);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Описание должно быть меньше 200 символов");
    }

    @Test
    public void addFilmAncientReleaseDAte() {
        RequestFilm film = new RequestFilm(
                "Valid name",
                "Valid description",
                LocalDate.of(1800, 1, 1),
                120);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Дата выпуска фильма должна быть после 28.12.1895 г.");
    }

    @Test
    public void addFilmNegativeDuration() {
        RequestFilm film = new RequestFilm(
                "Valid name",
                "Valid description",
                LocalDate.of(2022, 1, 1),
                -30);

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), film, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Продолжительность фильма должна быть положительной");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/films";
    }
}
