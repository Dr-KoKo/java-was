CREATE TABLE articles
(
    id        VARCHAR(255) PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    author_id VARCHAR(255) NOT NULL,
    content   TEXT
);
CREATE TABLE users
(
    user_id  VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) NOT NULL
);
INSERT INTO articles (id, title, author_id, content)
VALUES ('1', 'First Article', 'user1', 'This is the content of the first article.'),
       ('2', 'Second Article', 'user2', 'This is the content of the second article.');
INSERT INTO users (user_id, password, nickname)
VALUES ('user1', 'password1', 'User One'),
       ('user2', 'password2', 'User Two');
