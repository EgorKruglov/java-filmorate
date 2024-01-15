package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Event {
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private EventOperation operation;
    private Integer eventId;
    private Integer entityId;

    public Event(Integer userId, EventType eventType, EventOperation eventOperation, Integer entityId) {
        this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        this.userId = userId;
        this.eventType = eventType;
        this.operation = eventOperation;
        this.entityId = entityId;
    }
}
