DROP TABLE if EXISTS answer;
DROP TABLE if EXISTS checkbox;
DROP TABLE if EXISTS checkbox_group;
DROP TABLE if EXISTS question;
DROP TABLE if EXISTS question_group;
DROP TABLE if EXISTS survey;
DROP TABLE if EXISTS USER;

CREATE TABLE user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time DATETIME NULL,
    last_updated  DATETIME NULL,
    version       INT NULL,
    name          VARCHAR(255) NULL
);

CREATE TABLE survey
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time DATETIME NULL,
    last_updated  DATETIME NULL,
    version       INT NULL,
    accessid      VARCHAR(255) NULL,
    description   LONGTEXT NULL,
    end_date      DATETIME(6) NULL,
    name          VARCHAR(255) NOT NULL,
    open          BOOLEAN      NOT NULL,
    start_date    DATETIME(6) NULL,
    uuid          VARCHAR(255) NULL,
    user_id       BIGINT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE question_group
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time DATETIME NULL,
    last_updated  DATETIME NULL,
    version       INT NULL,
    title         VARCHAR(255) NULL,
    survey_id     BIGINT NULL,
    FOREIGN KEY (survey_id) REFERENCES survey (id)
);

CREATE TABLE question
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time     DATETIME NULL,
    last_updated      DATETIME NULL,
    version           INT NULL,
    has_checkbox      BOOLEAN      NOT NULL,
    required          BOOLEAN      NOT NULL,
    text              VARCHAR(255) NOT NULL,
    question_group_id BIGINT NULL,
    FOREIGN KEY (question_group_id) REFERENCES question_group (id)
);

CREATE TABLE checkbox_group
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time   DATETIME NULL,
    last_updated    DATETIME NULL,
    version         INT NULL,
    max_select      INT     NOT NULL,
    min_select      INT     NOT NULL,
    multiple_select BOOLEAN NOT NULL,
    question_id     BIGINT NULL,
    FOREIGN KEY (question_id) REFERENCES question (id)
);

CREATE TABLE checkbox
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation_time     DATETIME NULL,
    last_updated      DATETIME NULL,
    version           INT NULL,
    has_text_field    BOOLEAN      NOT NULL,
    text              VARCHAR(255) NOT NULL,
    checkbox_group_id BIGINT NULL,
    FOREIGN KEY (checkbox_group_id) REFERENCES checkbox_group (id)
);

CREATE TABLE answer
(
    id            BIGINT auto_increment PRIMARY KEY,
    creation_time DATETIME NULL,
    last_updated  DATETIME NULL,
    version       INT NULL,
    text          VARCHAR(255) NULL,
    checkbox_id   BIGINT NULL,
    question_id   BIGINT NULL,
    user_id       BIGINT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (question_id) REFERENCES question (id),
    FOREIGN KEY (checkbox_id) REFERENCES checkbox (id)
);






