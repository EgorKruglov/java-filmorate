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
import ru.yandex.practicum.filmorate.database.Users;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final Users users = new Users();

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid  @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации" + errors, new ValidationException());
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (user.getName() == null) { // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }

        user.setId(IdGenerator.generate());

        if (!users.addUser(user)) {
            log.warn("Не удалось добавить пользователя id:" + user.getId());
            return ResponseEntity.badRequest().body("Не удалось добавить пользователя");
        }

        log.info("Пользователь добавлен id:" + user.getId());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            log.info("Ошибка валидации" + errors, new ValidationException());
            return ResponseEntity.badRequest().body(errors.toString());
        }

        if (user.getName() == null) { // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }

        if (!users.updateUser(user)) {
            log.warn("Не удалось обновить данные пользователя id:" + user.getId());
            return ResponseEntity.badRequest().body("Не удалось обновить данные пользователя");
        }

        log.info("Данные пользователя обновлены id:" + user.getId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getUsers() {
        log.info("Отправлен список всех пользователей");
        return ResponseEntity.ok(users.getUsers());
    }
}
