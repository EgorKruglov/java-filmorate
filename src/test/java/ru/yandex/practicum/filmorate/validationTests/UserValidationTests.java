package ru.yandex.practicum.filmorate.validationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserValidationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testAllIsOk() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("valid_login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.OK;
    }

    @Test
    public void testInvalidEmail() {
        User user = new User();
        user.setEmail("invalid_email");
        user.setLogin("valid_login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Некорректный email адрес");
    }

    @Test
    public void testBlankEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("valid_login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Email не может быть пустым");
    }

    @Test
    public void testInvalidLogin() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("invalid login with spaces");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Login не может содержать пробелы");
    }

    @Test
    public void testBlankLogin() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("Login не может содержать пробелы");
    }

    @Test
    public void testEmptyDisplayName() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("valid_login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, User.class);
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody().getName().equals(user.getLogin());
    }

    @Test
    public void testFutureBirthday() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("valid_login");
        user.setBirthday(LocalDate.now().plusDays(1));

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/add", user, String.class);
        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;
        assert response.getBody().contains("День рождения не может быть в будущем");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/v1/users";
    }
}
