package ru.yandex.practicum.filmorate.util;

public class IdGenerator {

    private static int id = 1;

    public static int generate() {
        return  id++;
    }
}
