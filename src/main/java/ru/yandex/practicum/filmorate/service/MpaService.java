package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpaById(Integer mpaId) {
        log.info("Получен объект возрастного ограничения по ID: {}", mpaId);
        return mpaStorage.getMpaById(mpaId);
    }

    public List<Mpa> getAllMpa() {
        log.info("Получен список всех возрастных ограничений");
        return mpaStorage.getAllMpa();
    }
}
