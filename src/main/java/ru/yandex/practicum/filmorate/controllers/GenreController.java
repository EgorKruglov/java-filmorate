package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{genreId}")
    public Genre getGenre(@PathVariable Integer genreId) {
        Genre genre = genreService.getGenreById(genreId);
        log.info("Отправлена информация о жанре фильм id:" + genreId);
        return genre;
    }

    @GetMapping
    public List<Genre> getGenres() {
        List<Genre> genres = genreService.getGenres();
        log.info("Отправлен список всех жанров фильмов");
        return genres;
    }
}
