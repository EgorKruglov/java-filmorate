package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.UserIdEqualsFriendIdException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGeneratorUsers;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        if (user.getName().isBlank()) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        user.setId(IdGeneratorUsers.generate());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null) {  // Если нет имени, использовать логин
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при добавлении друга");
        }
        User friend = userStorage.getUserById(friendId);
        User user = userStorage.getUserById(userId);
        userStorage.getUserById(userId).addFriend(friend);
        userStorage.getUserById(friendId).addFriend(user);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при удалении друга");
        }
        User friend = userStorage.getUserById(friendId);
        if (!userStorage.getUserById(userId).checkFriends(friendId)) {
            throw new UserNotFoundException("У пользователя id:" + userId + " нет друга id:" + friendId);
        }
        userStorage.getUserById(userId).deleteFriend(friendId);
    }

    public List<User> getUserFriends(Integer userId) {
        return userStorage.getUsersFriends(userId);
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        if (userId.equals(otherId)) {
            throw new UserIdEqualsFriendIdException("Передано два одинаковых id при получении общих друзей");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }
}
