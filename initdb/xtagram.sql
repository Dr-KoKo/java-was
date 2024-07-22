CREATE TABLE IF NOT EXISTS articles
(
    id        VARCHAR(255) PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author_id VARCHAR(255) NOT NULL,
    content   TEXT,
    image     BLOB
);
CREATE TABLE  IF NOT EXISTS users
(
    user_id  VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL
);
INSERT INTO users (user_id, password, nickname)
VALUES ('user1', 'password1', 'User One'),
       ('user2', 'password2', 'User Two');
