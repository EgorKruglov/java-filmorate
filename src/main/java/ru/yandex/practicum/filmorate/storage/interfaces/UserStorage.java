package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public User addUser(User user);

    public User updateUser(User user);

    public List<User> getUsers();

    public User getUserById(Integer userId);

    public void addFriend(Integer userId, Integer friendId);

    public void deleteFriend(Integer userId, Integer friendId);

    public List<User> getUsersFriends(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);

    void deleteUser(Integer userId);
}
