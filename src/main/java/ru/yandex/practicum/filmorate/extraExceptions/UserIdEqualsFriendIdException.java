package ru.yandex.practicum.filmorate.extraExceptions;

public class UserIdEqualsFriendIdException extends RuntimeException{
    public UserIdEqualsFriendIdException(String message) {
        super(message);
    }
}
