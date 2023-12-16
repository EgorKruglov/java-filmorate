package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.UserIdEqualsFriendIdException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;

@Service
@Qualifier("userDbStorage")
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getName().isBlank()) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при добавлении друга");
        }
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при удалении друга");
        }
        if (userId < 0 || friendId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUsersFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (userId.equals(otherId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при получении общих друзей");
        }
        if (userId < 0 || otherId < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }
}
