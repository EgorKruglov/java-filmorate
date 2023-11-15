package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.modelsForRequest.RequestFilm;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    
    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public RequestFilm addFilm(@Valid @RequestBody RequestFilm requestFilm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors + " ", new ValidationException(errors.toString()));
            throw new ValidationException(errors.toString());
        }
        RequestFilm film = filmService.addFilm(requestFilm);
        log.info("Фильм добавлен id: " + film.getId());
        return film;
    }

    @PutMapping
    public RequestFilm updateFilm(@Valid @RequestBody RequestFilm requestFilm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors, new ValidationException(errors.toString()));
            throw new ValidationException(errors.toString());
        }
        RequestFilm film = filmService.updateFilm(requestFilm);
        log.info("Данные фильма обновлены id:" + film.getId());
        return film;
    }

    @GetMapping
    public List<RequestFilm> getFilms() {
        log.info("Отправлен список всех фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public RequestFilm getFilm(@PathVariable Integer filmId) {
        RequestFilm film = filmService.getFilmById(filmId);
        log.info("Отправлена информация о фильме id:" + filmId);
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Map<String, String> addLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
        log.info("На фильм id:" + filmId + " поставлен лайк пользователем id:" + userId);
        return Map.of("message", "На фильм id:" + filmId + " поставлен лайк пользователем id:" + userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Map<String, String> deleteLike(@PathVariable Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
        log.info("C фильма id:" + filmId + " удалён лайк пользователя id:" + userId);
        return Map.of("message", "C фильма id:" + filmId + " удалён лайк пользователя id:" + userId);
    }

    @GetMapping("/popular")
    public List<RequestFilm> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        List<RequestFilm> topFilms = filmService.getTopFilms(count);
        log.info("Отправлено " + topFilms.size() + " лучших фильмов по лайкам");
        return topFilms;
    }
}
