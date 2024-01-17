package ru.yandex.practicum.filmorate.extraExceptions;

public class UnknownSearchingParameterException extends RuntimeException {
    public UnknownSearchingParameterException(String message) {
        super(message);
    }
}