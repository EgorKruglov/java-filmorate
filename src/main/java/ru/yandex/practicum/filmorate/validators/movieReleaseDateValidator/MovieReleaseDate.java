package ru.yandex.practicum.filmorate.validators.movieReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MovieReleaseDateValidator.class)
@Documented
public @interface MovieReleaseDate {
    String message() default "Дата выпуска фильма должна быть после 28.12.1895 г.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
