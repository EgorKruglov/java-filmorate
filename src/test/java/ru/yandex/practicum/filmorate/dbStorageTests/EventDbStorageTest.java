package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    User newUser;
    private EventDbStorage eventDbStorage;
    private UserDbStorage userStorage;

    @BeforeEach
    public void updateDb() {
        eventDbStorage = new EventDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);

        newUser = new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990, 1, 1));
        userStorage.addUser(newUser);
    }

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM user_event");
    }

    @Test
    public void testAddFeed() {
        Event event = new Event(newUser.getId(), EventType.LIKE, EventOperation.ADD, 321);
        Event eventFromDb = eventDbStorage.add(event);

        assertThat(eventFromDb).isNotNull().usingRecursiveComparison().isEqualTo(event);
    }

    @Test
    public void testGetUserEvent() {
        Assertions.assertTrue(eventDbStorage.getUserEvent(1).isEmpty());

        Event event = new Event(newUser.getId(), EventType.LIKE, EventOperation.ADD, 321);
        eventDbStorage.add(event);

        Event event2 = new Event(newUser.getId(), EventType.FRIEND, EventOperation.ADD, 321);
        eventDbStorage.add(event2);

        Assertions.assertFalse(eventDbStorage.getUserEvent(newUser.getId()).isEmpty());

        Assertions.assertEquals(eventDbStorage.getUserEvent(newUser.getId()).size(), 2);

    }
}
