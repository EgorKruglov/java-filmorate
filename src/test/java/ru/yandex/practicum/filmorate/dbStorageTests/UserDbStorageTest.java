package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;

    @BeforeEach
    public void updateDb() {
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testAddUser() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(newUser);

        assertThat(addedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(newUser);

        addedUser.setName("Updated Name");
        User updatedUser = userStorage.updateUser(addedUser);

        assertThat(updatedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedUser);
    }

    @Test
    public void testGetUsers() {
        User user1 = new User("user1@email.ru", "user123", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User("user2@email.ru", "user456", "User Two", LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);
        userStorage.addUser(user2);

        List<User> users = userStorage.getUsers();

        assertThat(users).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(List.of(user1, user2));
    }

    @Test
    public void testFindUserById() {
        User newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(newUser);

        User savedUser = userStorage.getUserById(addedUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newUser);
    }

    @Test
    public void testAddFriend() {
        User user1 = new User("user1@email.ru", "user123", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User("user2@email.ru", "user456", "User Two", LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());

        List<User> friends = userStorage.getUsersFriends(addedUser1.getId());
        assertThat(friends).containsExactly(addedUser2);
    }

    @Test
    public void testDeleteFriend() {
        User user1 = new User("user1@email.ru", "user123", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User("user2@email.ru", "user456", "User Two", LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());
        userStorage.deleteFriend(addedUser1.getId(), addedUser2.getId());

        List<User> friends = userStorage.getUsersFriends(addedUser1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    public void testGetUsersFriends() {
        User user1 = new User("user1@email.ru", "user123", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User("user2@email.ru", "user456", "User Two", LocalDate.of(1990, 1, 1));
        User user3 = new User("user3@email.ru", "user788", "User Three", LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);
        User addedUser3 = userStorage.addUser(user3);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());
        userStorage.addFriend(addedUser1.getId(), addedUser3.getId());

        List<User> friends = userStorage.getUsersFriends(addedUser1.getId());
        assertThat(friends)
                .contains(addedUser2)
                .contains(addedUser3);
    }

    @Test
    public void testGetCommonFriends() {
        User user1 = new User("user1@email.ru", "user123", "User One", LocalDate.of(1990, 1, 1));
        User user2 = new User("user2@email.ru", "user456", "User Two", LocalDate.of(1990, 1, 1));
        User user3 = new User("user3@email.ru", "user789", "User Three", LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);
        User addedUser3 = userStorage.addUser(user3);

        userStorage.addFriend(addedUser1.getId(), addedUser3.getId());
        userStorage.addFriend(addedUser2.getId(), addedUser3.getId());

        List<User> commonFriends = userStorage.getCommonFriends(addedUser1.getId(), addedUser2.getId());
        assertThat(commonFriends).containsExactly(addedUser3);
    }
}
