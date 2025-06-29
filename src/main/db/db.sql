CREATE DATABASE quarkus-social
CREATE TABLE users(
id bigserial PRIMARY KEY NOT NULL,
nome VARCHAR(100),
idade INTEGER NOT NULL
);

CREATE TABLE posts(
id bigserial PRIMARY KEY NOT NULL,
post_text VARCHAR(150) NOT NULL,
dateTime TIMESTAMP NOT NULL,
user_id bigint NOT NULL  REFERENCES users(id)
);

CREATE TABLE FOLLOWERS(
id bigserial PRIMARY KEY NOT NULL,
user_id bigint NOT NULL REFERENCES USERS(id),
follower_id bigseial NOT NULL REFERENCES USERS(id)

)