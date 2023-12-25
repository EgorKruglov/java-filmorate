package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/* Класс для сохранения данных о пользователях приложения внутри базы данных H2 */
@Component
public class UserDbStorage  implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        try {
            int userId = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
            user.setId(userId);
            return user;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить пользователя");
        }
    }

    @Override
    public User updateUser(User user) {
        getUserById(user.getId());  // Проверка наличия пользователя
        String sqlQuery = "UPDATE users\n" +
                "SET name = ?,\n" +
                "    login = ?,\n" +
                "    birthday = ?,\n" +
                "    email = ?\n" +
                "WHERE user_id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    user.getName(),
                    user.getLogin(),
                    user.getBirthday(),
                    user.getEmail(),
                    user.getId());
            return user;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось обновить пользователя");
        }
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * " +
                "FROM users";
        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить список пользователей");
        }
    }

    @Override
    public User getUserById(Integer userId) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new UserNotFoundException("Пользователь c id " + userId + " не найден");
        }
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (checkFriendship(userId, friendId)) {  // Проверить, нет ли уже такой связи дружбы
            throw new SQLErrorTransaction("Пользователь id:" + friendId +
                    " уже является другом пользователя id:" + userId);
        }

        if (checkFriendship(friendId, userId)) {  // Добавить подтверждённую дружбу
            String sqlQuery = "INSERT INTO friendship(user_id1, user_id2, status)\n" +
                    "VALUES (?, ?, ?)";
            String sqlQuery2 = "UPDATE friendship\n" +
                    "SET user_id1 = ?,\n" +
                    "    user_id2 = ?,\n" +
                    "    status = ?\n" +
                    "WHERE friendship_id = ?";

            try {  // Добавить подтверждённую дружбу пользователю
                jdbcTemplate.update(sqlQuery, userId, friendId, "подтверждённая");
            } catch (DataAccessException e) {
                throw new SQLErrorTransaction("Не удалось создать дружбы пользователя id:" + userId +
                        " с пользователем id:" + friendId);
            }

            try {  // Обновить дружбу у друга
                jdbcTemplate.update(sqlQuery2,
                        friendId,
                        userId,
                        getFriendshipId(friendId, userId));
            } catch (DataAccessException e) {
                throw new SQLErrorTransaction("Не удалось обновить пользователя");
            }
            return;
        }

        String sqlQuery = "INSERT INTO friendship(user_id1, user_id2) " +
                "VALUES (?, ?)";


        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось создать дружбы пользователя id:" + userId +
                    " с пользователем id:" + friendId);
        }
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (!checkFriendship(userId, friendId)) {  // Проверить, есть ли такая дружба
            throw new SQLErrorTransaction("Пользователь id:" + friendId +
                    " не является другом пользователя id:" + userId);
        }

        if (checkFriendship(friendId, userId)) {  // Удалить подтверждённую дружбу
            String sqlQuery2 = "UPDATE friendship\n" +
                    "SET user_id1 = ?,\n" +
                    "    user_id2 = ?\n" +
                    "WHERE friendship_id = ?";

            try {  // Изменить дружбу у друга пользователя
                jdbcTemplate.update(sqlQuery2, friendId, userId, getFriendshipId(friendId, userId));
            } catch (DataAccessException e) {
                throw new SQLErrorTransaction("Не удалось изменить данные пользователя id:" + friendId +
                        " о дружбе с id:" + userId);
            }
        }
        String sqlQuery = "DELETE\n" +
                "FROM friendship\n" +
                "WHERE friendship_id = ?";
        try {  // Удалить у пользователя
            jdbcTemplate.update(sqlQuery, getFriendshipId(userId, friendId));
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось удалить данные дружбы пользователя id:" + userId +
                    " с пользователем id:" + friendId);
        }

    }

    @Override
    public List<User> getUsersFriends(Integer userId) {
        getUserById(userId);  // Проверка пользователя в бд

        String sqlQuery = "SELECT u.user_id,\n" +
                "u.name,\n" +
                "u.login,\n" +
                "u.birthday,\n" +
                "u.email " +
                "FROM friendship AS f JOIN users AS u ON f.user_id2 = u.user_id\n" +
                "WHERE f.user_id1 = ?";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось отправить друзей пользователя");
        }
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        getUserById(userId);  // Проверка пользователей в бд
        getUserById(otherId);

        String sqlQuery = "SELECT DISTINCT u.user_id,\n" +
                "u.name,\n" +
                "u.login,\n" +
                "u.birthday,\n" +
                "u.email\n" +
                "FROM friendship AS f1\n" +
                "JOIN friendship AS f2 ON f1.user_id2 = f2.user_id2\n" +
                "JOIN users AS u ON F1.user_id2 = u.user_id\n" +
                "WHERE f1.user_id1 = ? AND f2.user_id1 = ?\n" +
                "   OR f1.user_id1 = ? AND f2.user_id1 = ?;";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherId, otherId, userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось получить общих друзей пользователей id:" + userId + " и id:" + otherId);
        }
    }

    private Integer getFriendshipId(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT friendship_id\n" +
                "FROM friendship\n" +
                "WHERE user_id1 = ?\n" +
                "  AND user_id2 = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);

            if (rowSet.next()) {
                return rowSet.getInt("friendship_id");
            } else {
                throw new SQLErrorTransaction("Дружба между пользователем id:" + userId + " и id:" + friendId + " не найдена");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось проверить дружбу между пользователями");
        }
    }

    private Boolean checkFriendship(Integer userId, Integer friendId) {
        String sqlQuery = "SELECT friendship_id\n" +
                "FROM friendship\n" +
                "WHERE user_id1 = ?\n" +
                "  AND user_id2 = ?";
        try {
            SqlRowSet resultRow = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
            return resultRow.next();
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось проверить дружбу между пользователями");
        }
    }

    private User mapRowToUser(ResultSet resultSet, int i) {
        try {
            return new User(resultSet.getInt("user_id"),
                    resultSet.getString("email"),
                    resultSet.getString("login"),
                    resultSet.getString("name"),
                    resultSet.getDate("birthday").toLocalDate());
        } catch (SQLException e) {
            throw new SQLErrorTransaction("Не удалось создать объект пользователя на основе базы данных");
        }
    }
}
