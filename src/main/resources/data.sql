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

INSERT INTO films (name , description , release_date , duration , rating_id)
VALUES ('Тень', '30-ые годы XX века, город Нью-Йорк...', '1994-07-01', 108 , 3),
    ('Звёздные войны: Эпизод 4 – Новая надежда', 'Татуин. Планета-пустыня. Уже постаревший рыцарь Джедай ...', '1997-05-25', 121 , 2),
    ('Зеленая миля', 'Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора» ...', '1999-12-06', 189 , 4),
    ('Гадкий я', 'Гадкий снаружи, но добрый внутри Грю намерен, тем не менее, ...', '2010-06-27', 95 , 2);

INSERT INTO film_genre (film_id , genre_id)
VALUES (1, 6), (1, 1), (1, 2),
    (2, 6), (2, 5), (2, 3),
    (3, 3), (3, 5),
    (4, 1), (4, 6), (4, 4), (4, 3);

INSERT INTO users (email , login , name , birthday)
VALUES ('Capitan@yandex.ru', 'Capitan', 'Capitan', '2001-01-01'),
    ('Jack@yandex.ru', 'Jack', 'Jack', '2002-02-02'),
    ('Sparrow@yandex.ru', 'Sparrow', 'Sparrow', '2003-03-03');

INSERT INTO liked_user (film_id, user_id)
VALUES (1, 1),
    (2, 1), (2, 2), (2, 3),
    (3, 2),
    (4, 1), (4, 2);

INSERT INTO friends (sender, recipient, confirmed)
VALUES (1, 2, TRUE), (1, 3, TRUE),
    (2, 1, FALSE), (2, 3, TRUE),
    (3, 1, FALSE), (3, 2, TRUE);