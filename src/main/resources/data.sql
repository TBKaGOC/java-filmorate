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

INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('Film Updated', 'New film update decription', '1989-04-17', 190, 5);
INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES ('New film', 'New film about friends', '1999-04-30', 120, 3);

INSERT INTO users (email, login, name, birthday)
VALUES ('mail@yandex.ru', 'doloreUpdate', 'est adipisicing', '1976-09-20');
INSERT INTO users (email, login, name, birthday)
VALUES ('friend@mail.ru', 'friend', 'friend adipisicing', '1976-08-20');
INSERT INTO users (email, login, name, birthday)
VALUES ('friend@common.ru', 'common', 'common', '2000-08-20');

INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098162788', 1, 'FRIEND', 'ADD', 2);
INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098163227', 1, 'FRIEND', 'ADD', 3);
INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098163397', 2, 'FRIEND', 'ADD', 3);
INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098163668', 1, 'FRIEND', 'REMOVE', 2);
INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098233099', 1, 'LIKE', 'ADD', 2);
INSERT INTO events(timestamp, user_id, eventType, operation, entity_id)
VALUES ('1729098233281', 1, 'LIKE', 'REMOVE', 2);

INSERT INTO friends (sender, recipient, confirmed)
VALUES (1,	3, 'true');
INSERT INTO friends (sender, recipient, confirmed)
VALUES (2,	3, 'true');

INSERT INTO film_genre (film_id, genre_id)
VALUES (2, 1);
INSERT INTO film_genre (film_id, genre_id)
VALUES (2, 2);