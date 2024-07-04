ALTER TABLE question
    ADD CHECK (FALSE);
ALTER TABLE checkbox
    ADD CHECK (FALSE);
ALTER TABLE answer
    ADD CHECK (FALSE);

CREATE TABLE openQuestion
(
    id              bigserial NOT NULL PRIMARY KEY,
    creationTime    timestamp NOT NULL DEFAULT NOW(),
    lastUpdated     timestamp NOT NULL DEFAULT NOW(),
    version         integer   NOT NULL,
    questionGroupId bigint    NOT NULL REFERENCES questionGroup (id) ON DELETE CASCADE,
    rank            integer   NOT NULL,
    text            bytea     NOT NULL,
    required        boolean   NOT NULL
);

CREATE TABLE choiceQuestion
(
    id              bigserial NOT NULL PRIMARY KEY,
    creationTime    timestamp NOT NULL DEFAULT NOW(),
    lastUpdated     timestamp NOT NULL DEFAULT NOW(),
    version         integer   NOT NULL,
    questionGroupId bigint    NOT NULL REFERENCES questionGroup (id) ON DELETE CASCADE,
    rank            integer   NOT NULL,
    text            bytea     NOT NULL,
    minSelect       integer   NOT NULL,
    maxSelect       integer   NOT NULL,
    CONSTRAINT minmax_check CHECK ( minSelect >= 0 AND maxSelect >= choiceQuestion.minSelect )
);

CREATE TABLE choiceQuestionChoice
(
    id           bigserial NOT NULL PRIMARY KEY,
    creationTime timestamp NOT NULL DEFAULT NOW(),
    lastUpdated  timestamp NOT NULL DEFAULT NOW(),
    version      integer   NOT NULL,
    questionId   bigint    NOT NULL REFERENCES choiceQuestion (id) ON DELETE CASCADE,
    text         bytea     NOT NULL,
    hasTextField boolean   NOT NULL
);

CREATE TABLE openAnswer
(
    id           bigserial NOT NULL PRIMARY KEY,
    creationTime timestamp NOT NULL DEFAULT NOW(),
    lastUpdated  timestamp NOT NULL DEFAULT NOW(),
    version      integer   NOT NULL,
    questionId   bigint    NOT NULL REFERENCES openQuestion (id) ON DELETE CASCADE,
    responseId   bigint    NOT NULL REFERENCES surveyResponse (id) ON DELETE CASCADE,
    text         bytea     NOT NULL,
    UNIQUE (questionId, responseId)
);

CREATE TABLE choiceAnswer
(
    choiceId   bigint NOT NULL REFERENCES choiceQuestionChoice (id) ON DELETE CASCADE,
    responseId bigint NOT NULL REFERENCES surveyResponse (id) ON DELETE CASCADE,
    PRIMARY KEY (choiceId, responseId)
);

CREATE TABLE choiceAnswerText
(
    id           bigserial NOT NULL PRIMARY KEY,
    creationTime timestamp NOT NULL DEFAULT NOW(),
    lastUpdated  timestamp NOT NULL DEFAULT NOW(),
    version      integer   NOT NULL,
    questionId   bigint    NOT NULL REFERENCES choiceQuestion (id) ON DELETE CASCADE,
    responseId   bigint    NOT NULL REFERENCES surveyResponse (id) ON DELETE CASCADE,
    text         bytea     NOT NULL,
    UNIQUE (questionId, responseId)
);

DROP TABLE checkboxSelections, checkbox, checkboxGroup, answer, question;