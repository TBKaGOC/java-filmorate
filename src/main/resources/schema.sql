CREATE TABLE IF NOT EXISTS rating (
    rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS films (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR,
    description TEXT,
    release_date DATE,
    duration INTEGER,
    rating_id VARCHAR REFERENCES rating (rating_id)
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR,
    login VARCHAR,
    name VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS events (
    event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp BIGINT,
    user_id INTEGER REFERENCES users (id),
    eventType VARCHAR,
    operation VARCHAR,
    entity_id INTEGER
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS friends (
    sender int,
    recipient int,
    confirmed BOOL,
    foreign key (sender) references users (id) on delete cascade,
    foreign key (recipient) references users (id) on delete cascade,
    PRIMARY KEY(sender, recipient)
);

CREATE TABLE IF NOT EXISTS liked_user (
    film_id INTEGER REFERENCES films (id),
    user_id INTEGER REFERENCES users (id),
    PRIMARY KEY(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genre (genre_id),
    PRIMARY KEY(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR,
    isPositive int,
    film_id INTEGER REFERENCES films (id),
    user_id INTEGER REFERENCES users (id),
    CONSTRAINT relationFilmUser UNIQUE(user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviewLikes (
    review_id INTEGER REFERENCES reviews (review_id),
    user_id INTEGER REFERENCES users (id),
    useful INTEGER,
    PRIMARY KEY(review_id, user_id)
);

CREATE TABLE IF NOT EXISTS directors (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films_directors (
    film_id INTEGER REFERENCES films (id),
    director_id INTEGER REFERENCES directors (id),
    PRIMARY KEY (film_id, director_id)
);