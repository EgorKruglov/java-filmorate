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
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            throw new ValidationException("Ошибка валидации пользователя: " + errors);
        }
        User resultUser = userService.addUser(user);
        log.info("Пользователь добавлен id:" + user.getId());
        return resultUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            throw new ValidationException("Ошибка валидации пользователя: " + errors);
        }
        User resultUser = userService.updateUser(user);
        log.info("Данные пользователя обновлены id:" + user.getId());
        return resultUser;
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userService.getUsers();
        log.info("Отправлен список всех пользователей");
        return users;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        log.info("Отправлена информация о пользователе id:" + userId);
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public Map<String, String> addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        userService.addFriend(userId, friendId);
        log.info("Пользователи id:" + userId + " добавил в друзья пользователя id:" + friendId);
        return Map.of("message", "Пользователи id:" + userId + " добавил в друзья пользователя id:" + friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public Map<String, String> deleteFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        userService.deleteFriend(userId, friendId);
        log.info("Пользователи id:" + userId + " и id:" + friendId + " взаимно удалены из друзей");
        return Map.of("message", "Пользователи id:" + userId + " и id:" + friendId + " взаимно удалены из друзей");
    }

    @GetMapping("/{userId}/friends")
    public List<User> getUserFriends(@PathVariable Integer userId) {
        List<User> friends = userService.getUserFriends(userId);
        log.info("Отправлен список друзей пользователя id:" + userId);
        return friends;
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer userId, @PathVariable Integer otherId) {
        List<User> commonFriends = userService.getCommonFriends(userId, otherId);
        log.info("Отправлен список общих друзей пользователей id:" + userId + " и id:" + otherId);
        return commonFriends;
    }

    @GetMapping("/{userId}/feed")
    public List<Event> getUserFeed(@PathVariable Integer userId) {
        log.info("Отправлен список действий пользователя id:" + userId);
        return userService.getUserEvent(userId);
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getFilmRecommendations(@PathVariable Integer userId) {
        List<Film> recommendations = filmService.getFilmRecommendations(userId);
        log.info("Отправлен список рекомендованных фильмов пользователю id:" + userId);
        return recommendations;
    }

    @GetMapping("/{userId}/feed")
    public List<Event> getUserFeed(@PathVariable Integer userId) {
        log.info("Отправлен список действий пользователя id:" + userId);
        return userService.getUserEvent(userId);
    }
}
