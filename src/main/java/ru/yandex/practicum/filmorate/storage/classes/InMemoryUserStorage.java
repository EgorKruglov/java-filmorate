package ru.yandex.practicum.filmorate.storage.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class InMemoryUserStorage implements ru.yandex.practicum.filmorate.storage.interfaces.UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (users.containsKey(user.getId())) {
            log.warn("Пользователь с id " + user.getId() + " уже существует");
            throw new UserAlreadyExistException("Пользователь с id " + user.getId() + " уже существует");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        this.getUserById(user.getId());  // Проверка наличия пользователя без дублирования кода
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserById(Integer userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id " + userId + " не найден");
            throw new UserNotFoundException("Пользователь c id " + userId + " не найден");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getUsersFriends(Integer userId) {
        Set<Integer> friendsIds = users.get(userId).getFriends();
        List<User> friends = new ArrayList<>(friendsIds.size());
        for (Integer id : friendsIds) {
            friends.add(users.get(id));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getUserById(userId);
        getUserById(otherId);

        Set<Integer> friendsOfOne = users.get(userId).getFriends();
        Set<Integer> friendsOfTwo = users.get(otherId).getFriends();
        List<User> commonFriends = new ArrayList<>();
        if (friendsOfOne.size() < friendsOfTwo.size()) {  // Ветвление ради уменьшения итераций
            for (Integer id : friendsOfOne) {
                if (friendsOfTwo.contains(id)) {
                    commonFriends.add(users.get(id));
                }
            }
        } else {
            for (Integer id : friendsOfTwo) {
                if (friendsOfOne.contains(id)) {
                    commonFriends.add(users.get(id));
                }
            }
        }
        return commonFriends;
    }
}
