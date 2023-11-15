package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    public User addUser(User user);

    public User updateUser(User user);

    public Map<Integer, User> getUsers();

    public User getUserById(Integer userId);

    public List<User> getUsersFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);
}
