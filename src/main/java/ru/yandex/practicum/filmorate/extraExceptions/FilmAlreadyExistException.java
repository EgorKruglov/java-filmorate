package ru.yandex.practicum.filmorate.extraExceptions;

public class FilmAlreadyExistException extends RuntimeException {
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
