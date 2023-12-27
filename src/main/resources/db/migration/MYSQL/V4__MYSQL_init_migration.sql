CREATE
SEQUENCE categories_seq START
WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS ads
(
    id UUID NOT NULL,
    title           VARCHAR(255),
    description CLOB,
    price           DOUBLE PRECISION NOT NULL,
    local_date_time TIMESTAMP,
    zipcode         VARCHAR(5),
    user_id UUID,
    category_id     BIGINT,
    CONSTRAINT pk_ads PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT NOT NULL,
    name VARCHAR(255),
    description CLOB,
    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS logs
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    timestamp TIMESTAMP,
    endpoint  VARCHAR(255),
    type      VARCHAR(255),
    data      VARCHAR(255),
    CONSTRAINT pk_logs PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id UUID NOT NULL,
    username                 VARCHAR(255),
    password                 VARCHAR(255),
    email                    VARCHAR(255),
    email_verified           BOOLEAN NOT NULL,
    email_verification_token VARCHAR(255),
    refresh_token            VARCHAR(255),
    roles                    VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE categories
    ADD CONSTRAINT uc_categories_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE ads
    ADD CONSTRAINT FK_ADS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE ads
    ADD CONSTRAINT FK_ADS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);