package ru.yandex.practicum.filmorate.storage.classes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event add(Event event) {
        String query = "INSERT INTO USER_EVENT (INSERT_DATE_TIME, USER_ID, EVENT_TYPE_ID, EVENT_OPERATION_ID, ENTITY_ID) " + " VALUES(? , ? , ? , ? , ?)";
        jdbcTemplate.update(query, event.getTimestamp(), event.getUserId(), event.getEventType().getId(), event.getOperation().getId(), event.getEntityId());
        return event;
    }

    @Override
    public List<Event> getUserEvent(Integer userId) {
        String query = "SELECT UE.EVENT_ID, UE.INSERT_DATE_TIME, UE.USER_ID,UE.ENTITY_ID, ET.NAME AS EVENT_TYPE_NAME, EO.NAME AS EVENT_OPERATION_NAME " + "FROM USER_EVENT AS UE LEFT JOIN EVENT_TYPE AS ET ON UE.EVENT_TYPE_ID = ET.EVENT_TYPE_ID " + "LEFT JOIN EVENT_OPERATION AS EO " + "ON UE.EVENT_OPERATION_ID = EO.EVENT_OPERATION_ID " + "WHERE UE.USER_ID=?";
        return jdbcTemplate.query(query, this::makeEvent, userId);
    }

    private Event makeEvent(ResultSet rs, int i) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setTimestamp(rs.getLong("insert_date_time"));
        event.setUserId(rs.getInt("user_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type_name")));
        event.setOperation(EventOperation.valueOf(rs.getString("event_operation_name")));
        event.setEntityId(rs.getInt("entity_id"));
        return event;
    }
}
