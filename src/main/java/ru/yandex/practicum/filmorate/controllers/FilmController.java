package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.database.FilmStorage;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.modelsForRequest.RequestFilm;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/films")
public class FilmController {

    private final FilmStorage films = new FilmStorage();

    @PostMapping()
    public ResponseEntity<?> addFilm(@Valid @RequestBody RequestFilm requestFilm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors + " ", new ValidationException(errors.toString()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors.toString()));
        }

        requestFilm.setId(IdGenerator.generate());
        Film film = new Film(requestFilm.getId(), // Создаём настоящий объект фильма из класса-обёртки
                requestFilm.getName(),
                requestFilm.getDescription(),
                requestFilm.getReleaseDate(),
                Duration.ofMinutes(requestFilm.getDuration()));

        if (!films.addFilm(film)) {
            log.info("Не удалось добавить фильм id:" + film.getId());
            return ResponseEntity.badRequest().body(Map.of("errors", "Не удалось добавить фильм"));
        }

        log.info("Фильм добавлен id:" + film.getId());

        return ResponseEntity.ok(requestFilm);
    }

    @PutMapping()
    public ResponseEntity<?> updateFilm(@Valid @RequestBody RequestFilm requestFilm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors, new ValidationException(errors.toString()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors.toString()));
        }

        Film film = new Film(requestFilm.getId(), // Создаём настоящий объект фильма из класса-обёртки
                requestFilm.getName(),
                requestFilm.getDescription(),
                requestFilm.getReleaseDate(),
                Duration.ofMinutes(requestFilm.getDuration()));

        if (!films.updateFilm(film)) {
            log.info("Не удалось обновить данные фильма id:" + film.getId());
            return ResponseEntity.status(404).body(Map.of("errors", "Не удалось обновить данные фильма"));
        }

        log.info("Данные фильма обновлены id:" + film.getId());
        return ResponseEntity.ok(requestFilm);
    }

    @GetMapping()
    public ResponseEntity<?> getFilms() {
        Map<Integer, Film> allFilms = films.getFilms();
        ArrayList<RequestFilm> requestFilms = new ArrayList<>();

        for (Film film : allFilms.values()) {
            RequestFilm requestFilm = new RequestFilm(film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration().toMinutes());
            requestFilm.setId(film.getId());
            requestFilms.add(requestFilm); // Заворачиваем фильмы в обёртки, где продолжительность фильма - число.
        }

        log.info("Отправлен список всех фильмов");
        return ResponseEntity.ok(requestFilms);
    }
}
