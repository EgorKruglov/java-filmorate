package ru.yandex.practicum.filmorate.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @PostMapping
    public Director createDirector(@RequestBody Director director) {
        log.info("Пришел POST-запрос /directors с телом: {}", director);
        Director createdDirector = directorService.createDirector(director);
        log.info("Отправлен ответ на POST-запрос /directors с телом: {}", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("Пришел PUT-запрос /directors с телом: {}", director);
        Director updatedDirector = directorService.updateDirector(director);
        log.info("Отправлен ответ на PUT-запрос /directors с телом: {}", director);
        return updatedDirector;
    }

    @GetMapping
    public List<Director> getAllDirector() {
        log.debug("Пришел запрос GET /directors");
        List<Director> foundedDirectors = directorService.getDirectorsList();
        log.debug("Отправлен овтет на GET запрос /directors с телом: {}", foundedDirectors);
        return foundedDirectors;
    }

    @GetMapping("/{directorId}")
    public Director getDirector(@PathVariable Long directorId) {
        log.info("Получен GET-запрос /directors/{}", directorId);
        Director foundedDirector = directorService.getDirectorById(directorId);
        log.info("Отправлен ответ на GET-запрос /directors/{} с телом: {}", directorId, foundedDirector);
        return foundedDirector;
    }

    @DeleteMapping("/{directorId}")
    public String deleteDirector(@PathVariable Long directorId) {
        log.info("Получен DELETE-запрос /directors/{}", directorId);
        log.info("Отправлен ответ на DELETE-запрос /directors/{}", directorId);
        return directorService.deleteDirector(directorId);
    }
}
