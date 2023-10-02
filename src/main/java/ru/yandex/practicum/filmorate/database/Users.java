package ru.yandex.practicum.filmorate.database;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public class Users {

    HashMap<Integer, User> users = new HashMap<>();

    public Boolean addUser(User user) {
        if (users.containsKey(user.getId())) {
            return false;
        }
        users.put(user.getId(), user);
        return true;
    }

    public Boolean updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            return false;
        }
        users.put(user.getId(), user);
        return true;
    }

    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
