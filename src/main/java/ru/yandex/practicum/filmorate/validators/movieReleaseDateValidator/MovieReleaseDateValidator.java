package ru.yandex.practicum.filmorate.validators.movieReleaseDateValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class MovieReleaseDateValidator implements ConstraintValidator<MovieReleaseDate, LocalDate> {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(MovieReleaseDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext context) {
        if (releaseDate == null) {
            return true; // На null будет проверка в другом валидаторе
        }

        return releaseDate.isAfter(MIN_RELEASE_DATE);
    }
}
