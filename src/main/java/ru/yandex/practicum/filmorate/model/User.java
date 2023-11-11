package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
public class User {

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
}
