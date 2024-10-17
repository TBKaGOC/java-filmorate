SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE film_genre;

TRUNCATE TABLE films_directors;

TRUNCATE TABLE friends;

TRUNCATE TABLE liked_user;

TRUNCATE TABLE films RESTART IDENTITY;

TRUNCATE TABLE users RESTART IDENTITY;

TRUNCATE TABLE genre RESTART IDENTITY;

TRUNCATE TABLE rating RESTART IDENTITY;

TRUNCATE TABLE directors RESTART IDENTITY;

SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO genre (name)
VALUES ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO rating (name)
VALUES ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');
