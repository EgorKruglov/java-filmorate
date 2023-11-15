package ru.yandex.practicum.filmorate.model.modelsForRequest;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.movieReleaseDateValidator.MovieReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/*Это вспомогательный класс-обёртка для получения фильма из запроса и отправки в ответе.
* Основная причина его создания -- это необходимость получения и передачи duration в минутах, когда
* duration тип Duration. */
@Data
public class RequestFilm {

    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;

    @Size(max = 200, message = "Описание должно быть меньше 200 символов")
    private final String description;

    @MovieReleaseDate
    private final LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final long duration;
}

