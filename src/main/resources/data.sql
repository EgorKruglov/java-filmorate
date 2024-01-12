INSERT INTO mpa (name) VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');

INSERT INTO genres (name) VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO event_type (event_type_id, name)
                  VALUES (1, 'LIKE'),
                         (2, 'REVIEW'),
                         (3, 'FRIEND');

INSERT INTO event_operation (event_operation_id, name)
                  VALUES (1, 'ADD'),
                         (2, 'UPDATE'),
                         (3, 'REMOVE');