package ru.yandex.practicum.filmorate.util;

public class IdGeneratorFilms {

    private static int id = 1;

    public static int generate() {
        return  id++;
    }
}
