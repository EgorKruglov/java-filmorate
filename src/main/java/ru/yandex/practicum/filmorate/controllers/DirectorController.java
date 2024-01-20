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
        log.info("Получен запрос на создание режиссера {}", director);
        Director createdDirector = directorService.createDirector(director);
        log.info("Создан режессер {}", createdDirector);
        return createdDirector;
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.info("Пришел запрос на обновление информации о режиссёре {}", director);
        Director updatedDirector = directorService.updateDirector(director);
        log.info("Информация о режиссёре {} обновлена", director);
        return updatedDirector;
    }

    @GetMapping
    public List<Director> getAllDirector() {
        List<Director> foundedDirectors = directorService.getDirectorsList();
        log.debug("Отправлен список режессёров {}", foundedDirectors);
        return foundedDirectors;
    }

    @GetMapping("/{directorId}")
    public Director getDirector(@PathVariable Long directorId) {
        Director foundedDirector = directorService.getDirectorById(directorId);
        log.info("Отправлена информация о режессёре с Id {}: {}", directorId, foundedDirector);
        return foundedDirector;
    }

    @DeleteMapping("/{directorId}")
    public String deleteDirector(@PathVariable Long directorId) {
        log.info("Удален директор с Id {}", directorId);
        return directorService.deleteDirector(directorId);
    }
}
