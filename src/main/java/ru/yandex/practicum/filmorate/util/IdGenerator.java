package ru.yandex.practicum.filmorate.util;

public class IdGenerator {

    private static int id = 0;

    static public int generate() {
        return  id++;
    }
}
