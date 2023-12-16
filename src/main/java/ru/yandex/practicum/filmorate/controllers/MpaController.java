package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{mpaId}")
    public Mpa getMpa(@PathVariable Integer mpaId) {
        Mpa mpa = mpaService.getMpaById(mpaId);
        log.info("Отправлена информация о возрастном ограничении id:" + mpaId);
        return mpa;
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        List<Mpa> mpaList = mpaService.getAllMpa();
        log.info("Отправлен список всех возрастных ограничений");
        return mpaList;
    }
}
