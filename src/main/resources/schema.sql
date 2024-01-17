DROP TABLE IF EXISTS film_likes;
DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS film_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS review_likes;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS user_event;
DROP TABLE IF EXISTS event_type;
DROP TABLE IF EXISTS event_operation;
DROP TABLE IF EXISTS users;
DROP TABLE if EXISTS director CASCADE;
DROP TABLE if EXISTS director_films CASCADE;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;

-- -----------------------------------------------------
-- Table mpa
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS mpa (
  mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(45) NOT NULL
);

-- -----------------------------------------------------
-- Table films
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS films (
  film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  release_date DATE,
  duration INT NOT NULL,
  mpa_id INT NOT NULL,
  CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id) REFERENCES mpa (mpa_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table genres
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS genres (
  genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

-- -----------------------------------------------------
-- Table film_genres
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS film_genres (
  film_id INT NOT NULL,
  genre_id INT NOT NULL,
  CONSTRAINT fk_film_genres_films FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT fk_film_genres_genres FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  login VARCHAR(255) NOT NULL,
  birthday DATE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL
);

-- -----------------------------------------------------
-- Table friendship
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS friendship (
  friendship_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  status VARCHAR(45) NOT NULL DEFAULT 'неподтверждённая',
  user_id1 INT NOT NULL,
  user_id2 INT NOT NULL,
  CONSTRAINT fk_friends_users1 FOREIGN KEY (user_id1) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT fk_friends_users2 FOREIGN KEY (user_id2) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table film_likes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS film_likes (
  user_id INT NOT NULL,
  film_id INT NOT NULL,
  CONSTRAINT fk_film_likes_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT fk_film_likes_films FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE NO ACTION
);
-- -----------------------------------------------------
-- Table director
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS director (
  id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar NOT NULL
);
-- -----------------------------------------------------
-- Table director_films
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS director_films (
  film_id     INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
  director_id INTEGER REFERENCES director (id) ON DELETE CASCADE,
  PRIMARY KEY (film_id, director_id)
);

-- -----------------------------------------------------
-- Table reviews
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS reviews (
  review_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content VARCHAR(10000) NOT NULL,
  is_positive BOOLEAN NOT NULL,
  user_id INT NOT NULL,
  film_id INT NOT NULL,
  CONSTRAINT fk_reviews_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT fk_reviews_films FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table review_likes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS review_likes (
  like_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  review_id INT NOT NULL,
  user_id INT NOT NULL,
  is_like BOOLEAN NOT NULL,  -- лайк/дизлайк
  CONSTRAINT fk_review_likes_reviews FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT fk_review_likes_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- -----------------------------------------------------
-- Table event_type
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS event_type (
        event_type_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name VARCHAR(10)
);

-- -----------------------------------------------------
-- Table event_operation
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS event_operation (
        event_operation_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        name VARCHAR(10)
);

-- -----------------------------------------------------
-- Table user_event
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS user_event (
        event_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
        insert_date_time BIGINT,
        user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
        event_type_id INTEGER REFERENCES event_type(event_type_id) ON DELETE CASCADE ON UPDATE CASCADE,
        event_operation_id INTEGER REFERENCES event_operation(event_operation_id) ON DELETE CASCADE ON UPDATE CASCADE,
        entity_id INTEGER
);


