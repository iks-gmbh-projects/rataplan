CREATE TABLE surveyToolUser
(
    id           SERIAL PRIMARY KEY,
    creationTime TIMESTAMP    NOT NULL,
    lastUpdated  TIMESTAMP    NOT NULL,
    version      INT          NOT NULL,
    name         VARCHAR(255) NOT NULL
);

CREATE TABLE survey
(
    id              SERIAL PRIMARY KEY,
    creationTime    TIMESTAMP     NOT NULL,
    lastUpdated     TIMESTAMP     NOT NULL,
    version         INT           NOT NULL,
    name            VARCHAR(255)  NOT NULL,
    description     VARCHAR(3000) NULL,
    startDate       TIMESTAMP(6)  NOT NULL,
    endDate         TIMESTAMP(6)  NOT NULL,
    openAccess      BOOLEAN       NOT NULL,
    accessId        VARCHAR(255)  NOT NULL,
    participationId VARCHAR(255)  NOT NULL,
    userId          BIGINT,
    FOREIGN KEY (userId) REFERENCES surveyToolUser (id),
    CONSTRAINT checkStartBeforeEnd CHECK ( startDate < endDate )
);

CREATE TABLE questionGroup
(
    id           SERIAL PRIMARY KEY,
    creationTime TIMESTAMP    NOT NULL,
    lastUpdated  TIMESTAMP    NOT NULL,
    version      INT          NOT NULL,
    title        VARCHAR(255) NOT NULL,
    surveyId     BIGINT REFERENCES survey (id)
);

CREATE TABLE question
(
    id              SERIAL PRIMARY KEY,
    creationTime    TIMESTAMP    NOT NULL,
    lastUpdated     TIMESTAMP    NOT NULL,
    version         INT          NOT NULL,
    hasCheckbox     BOOLEAN      NOT NULL,
    required        BOOLEAN      NOT NULL,
    text            VARCHAR(255) NOT NULL,
    questionGroupId BIGINT,
    FOREIGN KEY (questionGroupId) REFERENCES questionGroup (id)
);

CREATE TABLE checkboxGroup
(
    id             SERIAL PRIMARY KEY,
    creationTime   TIMESTAMP NOT NULL,
    lastUpdated    TIMESTAMP NOT NULL,
    version        INT       NOT NULL,
    multipleSelect BOOLEAN   NOT NULL,
    maxSelect      INT       NOT NULL,
    minSelect      INT       NOT NULL,
    questionId     BIGINT,
    FOREIGN KEY (questionId) REFERENCES question (id),
    CONSTRAINT maxSelectGreaterThanEqualsMinSelect CHECK ( minSelect <= maxSelect ),
    CONSTRAINT minSelectGreaterThanEqualsZero CHECK ( 0 <= minSelect ),
    CONSTRAINT maxSelectGreaterThanEqualsTwo CHECK ( 2 <= maxSelect )
);

CREATE TABLE checkbox
(
    id              SERIAL PRIMARY KEY,
    creationTime    TIMESTAMP    NOT NULL,
    lastUpdated     TIMESTAMP    NOT NULL,
    version         INT          NOT NULL,
    hasTextField    BOOLEAN      NOT NULL,
    text            VARCHAR(255) NOT NULL,
    checkboxGroupId BIGINT,
    FOREIGN KEY (checkboxGroupId) REFERENCES checkboxGroup (id)
);

CREATE TABLE answer
(
    id           SERIAL PRIMARY KEY,
    creationTime TIMESTAMP NOT NULL,
    lastUpdated  TIMESTAMP NOT NULL,
    version      INT       NOT NULL,
    text         VARCHAR(1500),
    checkboxId   BIGINT,
    questionId   BIGINT,
    userId       BIGINT,
    FOREIGN KEY (userId) REFERENCES surveyToolUser (id),
    FOREIGN KEY (questionId) REFERENCES question (id),
    FOREIGN KEY (checkboxId) REFERENCES checkbox (id)
);






