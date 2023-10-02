package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.movieReleaseDateValidator.MovieReleaseDate;
import ru.yandex.practicum.filmorate.validators.nonNegativeDurationValidator.PositiveDuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание должно быть меньше 200 символов")
    private String description;

    @MovieReleaseDate
    private LocalDate releaseDate;

    @PositiveDuration(message = "Продолжительность фильма должна быть положительным числом")
    private Duration duration;
}
