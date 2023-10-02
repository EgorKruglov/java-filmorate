package ru.yandex.practicum.filmorate.validators.nonNegativeDurationValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public class PositiveDurationValidator implements ConstraintValidator<PositiveDuration, Duration> {
    @Override
    public void initialize(PositiveDuration constraintAnnotation) {
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        return value.getSeconds() > 0;
    }
}

