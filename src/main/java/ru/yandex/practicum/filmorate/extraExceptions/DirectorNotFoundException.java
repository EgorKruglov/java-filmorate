package ru.yandex.practicum.filmorate.extraExceptions;

public class DirectorNotFoundException extends RuntimeException {
    public DirectorNotFoundException(String message) {
        super(message);
    }
}