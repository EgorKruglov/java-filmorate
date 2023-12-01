package ru.yandex.practicum.filmorate.util;

public class IdGeneratorUsers {

    private static int id = 1;

    public static int generate() {
        return  id++;
    }

    public static int getId() {
        return id;
    }
}
