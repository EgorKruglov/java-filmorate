package ru.yandex.practicum.filmorate.util;

public class IdGenerator {

    private static int id = 0;

    public static int generate() {
        return  id++;
    }
}
