package ru.yandex.practicum.filmorate.extraExceptions;

public class ValidationException extends RuntimeException {

    public ValidationException() {}

    public ValidationException(String message) {
        super(message);
    }
}
