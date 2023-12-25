package ru.yandex.practicum.filmorate.extraExceptions;

import org.springframework.dao.DataAccessException;

public class SQLErrorTransaction extends DataAccessException {

    public SQLErrorTransaction(String message) {
        super(message);
    }
}
