package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.database.Films;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/films")
public class FilmController {

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Films films = new Films();

    @PostMapping("/add")
    public ResponseEntity<?> addFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации" + errors, new ValidationException());
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (!FilmValidator.isValid(film)) {
            log.info("Ошибка валидации", new ValidationException());
            return ResponseEntity.badRequest().body("Некорректные данные фильма");
        }

        film.setId(IdGenerator.generate());

        if (!films.addFilm(film)) {
            log.info("Не удалось добавить фильм id:" + film.getId());
            return ResponseEntity.badRequest().body("Не удалось добавить фильм");
        }

        log.info("Фильм добавлен id:" + film.getId());
        return ResponseEntity.ok(film);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации" + errors, new ValidationException());
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (!films.updateFilm(film)) {
            log.info("Не удалось обновить данные фильма id:" + film.getId());
            return ResponseEntity.badRequest().body("Не удалось обновить данные фильма");
        }

        log.info("Данные фильма обновлены id:" + film.getId());
        return ResponseEntity.ok(film);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getFilms() {
        log.info("Отправлен список всех фильмов");
        return ResponseEntity.ok(films.getFilms());
    }
}
