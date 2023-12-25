package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    public Mpa getMpaById(Integer mpaId);

    public List<Mpa> getAllMpa();
}
