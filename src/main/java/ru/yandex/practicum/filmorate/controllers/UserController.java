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
import ru.yandex.practicum.filmorate.database.UserStorage;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGeneratorUsers;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage users = new UserStorage();

    @PostMapping
    public ResponseEntity<?> addUser(@Valid  @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors + " ", new ValidationException(errors.toString()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors.toString()));
        }

        if (user.getName() == null) { // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }

        user.setId(IdGeneratorUsers.generate());

        if (!users.addUser(user)) {
            log.warn("Не удалось добавить пользователя id:" + user.getId());
            return ResponseEntity.badRequest().body(Map.of("errors", "Не удалось добавить пользователя"));
        }

        log.info("Пользователь добавлен id:" + user.getId());
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации " + errors + " ", new ValidationException(errors.toString()));
            return ResponseEntity.badRequest().body(Map.of("errors", errors.toString()));
        }

        if (user.getName() == null) { // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }

        if (!users.updateUser(user)) {
            log.warn("Не удалось обновить данные пользователя id:" + user.getId());
            return ResponseEntity.status(404).body(Map.of("errors", "Не удалось обновить данные пользователя"));
        }

        log.info("Данные пользователя обновлены id:" + user.getId());
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<?> getUsers() {
        log.info("Отправлен список всех пользователей");
        return ResponseEntity.ok(users.getUsers().values());
    }
}
