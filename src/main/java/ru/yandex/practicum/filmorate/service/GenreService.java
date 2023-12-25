package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenreById(Integer genreId) {
        log.info("Получен жанр по ID: {}", genreId);
        return genreStorage.getGenreById(genreId);
    }

    public List<Genre> getGenres() {
        log.info("Получен список всех жанров");
        return genreStorage.getGenres();
    }
}
