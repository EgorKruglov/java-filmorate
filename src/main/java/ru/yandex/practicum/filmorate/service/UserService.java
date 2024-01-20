package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.UserIdEqualsFriendIdException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(UserDbStorage userStorage, EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public User addUser(User user) {
        if (user.getName().isBlank()) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        log.info("Добавление нового пользователя: {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        log.info("Обновление пользователя: {}", user);
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        log.info("Получение списка всех пользователей");
        return userStorage.getUsers();
    }

    public User getUserById(Integer userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Получение пользователя по id: {}", userId);
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при добавлении друга");
        }
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        userStorage.addFriend(userId, friendId);
        Event event = new Event(userId, EventType.FRIEND, EventOperation.ADD, friendId);
        eventStorage.add(event);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при удалении друга");
        }
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Удаление пользователя id:{} из друзей пользователя id:{}", friendId, userId);
        userStorage.deleteFriend(userId, friendId);
        Event event = new Event(userId, EventType.FRIEND, EventOperation.REMOVE, friendId);
        eventStorage.add(event);
    }

    public List<User> getUserFriends(Integer userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Получение списка друзей пользователя с id" + userId);
        return userStorage.getUsersFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (userId.equals(otherId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при получении общих друзей");
        }
        if (userId < 0 || otherId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Получение списка общих друзей пользователей {} и {}", userId, otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<Event> getUserEvent(Integer userId) {
        log.info("Получение списка действий пользователя c id:" + userId);
        getUserById(userId);
        return eventStorage.getUserEvent(userId);
    }

    public void deleteUser(Integer userId) {
        if (userId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Удаление пользователя c id:" + userId);
        userStorage.deleteUser(userId);
    }
}
