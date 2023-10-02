package ru.yandex.practicum.filmorate.database;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public class Films {

    HashMap<Integer, Film> films = new HashMap<>();

    public Boolean addFilm(Film film) {
        if (films.containsKey(film.getId())) {
            return false;
        }
        films.put(film.getId(), film);
        return true;
    }

    public Boolean updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            return false;
        }
        films.put(film.getId(), film);
        return true;
    }

    public HashMap<Integer, Film> getFilms() {
        return films;
    }
}
