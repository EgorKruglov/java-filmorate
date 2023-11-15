package ru.yandex.practicum.filmorate.extraExceptions;

public class FilmNotFoundException extends RuntimeException{

    public FilmNotFoundException() {
    }

    public FilmNotFoundException(String message) {
        super(message);
    }
}
