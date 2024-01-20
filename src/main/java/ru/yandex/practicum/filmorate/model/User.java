package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class User {
    @Positive
    private Integer id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email адрес")
    private String email;

    @NotBlank(message = "Login не может быть пустым")
    @Pattern(regexp = "^(?!\\s*$)[\\S]+", message = "Login не может содержать пробелы")
    private String login;

    private String name;
    @Past(message = "День рождения не может быть в будущем")
    private LocalDate birthday;

    public User(Integer id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public Map<String,Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("login", login);
        userMap.put("name", name);
        userMap.put("birthday", birthday);
        return userMap;
    }
}
