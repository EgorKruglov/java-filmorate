package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    static LocalDate minimumReleaseDate = LocalDate.of(1895, 12, 28);
    public static boolean isValid(Film film) {
        if (film.getReleaseDate().isBefore(minimumReleaseDate)) {
            return false;
        }

        if (film.getDuration().isNegative()) {
            return false;
        }

        return true;
    }
}
