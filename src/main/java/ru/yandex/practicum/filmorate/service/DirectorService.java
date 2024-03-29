package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {
    private final DirectorStorage directorStorage;

    public Director createDirector(Director director) {
        validateDirectorsName(director);
        log.info("Добавление нового режиссёрая: {}", director);
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        if (directorStorage.checkDirectorExistInDb(director.getId())) {
            log.info("Обновление режиссёра: {}", director);
            return directorStorage.updateDirector(director);
        } else {
            throw new DirectorNotFoundException("Режиссёр не найден");
        }
    }

    public Director getDirectorById(Long id) {
        if (directorStorage.checkDirectorExistInDb(id)) {
            log.info("Получение режиссёра по id: {}", id);
            return directorStorage.getDirectorById(id);
        } else {
            throw new DirectorNotFoundException("Режиссёр не найден");
        }
    }

    public List<Director> getDirectorsList() {
        log.info("Получение списка всех режиссёров");
        return directorStorage.getDirectorsList();
    }

    public String deleteDirector(Long id) {
        if (directorStorage.checkDirectorExistInDb(id)) {
            log.info("Удаление режиссёра id:{}", id);
            directorStorage.deleteDirector(id);
            return String.format("Режиссёр с id %s удалён", id);
        } else {
            throw new DirectorNotFoundException("Режиссёр не найден");
        }
    }

    public void validateDirectorsName(Director director) {
        if (!director.getName().isBlank() && !director.getName().equals(" ")) {
            log.info("Проверка режиссёра пройдена");
        } else {
            throw new ValidationException("Ошибка валидации.");
        }
    }
}