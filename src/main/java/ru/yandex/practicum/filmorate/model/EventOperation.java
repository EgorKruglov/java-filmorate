package ru.yandex.practicum.filmorate.model;

public enum EventOperation {
    ADD(1), UPDATE(2), REMOVE(3);
    private final int id;

    EventOperation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
