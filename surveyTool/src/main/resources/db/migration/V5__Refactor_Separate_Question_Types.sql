ALTER TABLE question
    ADD CHECK (text LIKE 'ENC\_\_##\_\_%' ESCAPE '\');
ALTER TABLE checkbox
    ADD CHECK (text LIKE 'ENC\_\_##\_\_%' ESCAPE '\');
ALTER TABLE answer
    ADD CHECK (text IS NULL OR text LIKE 'ENC\_\_##\_\_%' ESCAPE '\');

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

INSERT INTO openQuestion (id, creationTime, lastUpdated, version, questionGroupId, rank, text, required)
SELECT id,
       creationTime,
       lastUpdated,
       version,
       questionGroupId,
       id,
       DECODE(SUBSTR(text, 10), 'base64'),
       required
FROM question
WHERE NOT hascheckbox;

INSERT INTO choiceQuestion (id, creationTime, lastUpdated, version, questionGroupId, rank, text, minSelect, maxSelect)
SELECT question.id,
       LEAST(question.creationTime, checkboxGroup.creationTime),
       GREATEST(question.lastUpdated, checkboxGroup.lastUpdated),
       GREATEST(question.version, checkboxGroup.version),
       questionGroupId,
       question.id,
       DECODE(SUBSTR(text, 10), 'base64'),
       minSelect,
       maxSelect
FROM question
LEFT JOIN checkboxGroup
ON question.id = checkboxGroup.questionid
WHERE hascheckbox;

INSERT INTO choiceQuestionChoice (id, creationTime, lastUpdated, version, questionId, text, hasTextField)
SELECT checkbox.id,
       checkbox.creationTime,
       checkbox.lastUpdated,
       checkbox.version,
       questionId,
       DECODE(SUBSTR(text, 10), 'base64'),
       hasTextField
FROM checkbox
LEFT JOIN checkboxGroup
ON checkbox.checkboxgroupid = checkboxGroup.id;

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

INSERT INTO openAnswer (id, creationTime, lastUpdated, version, questionId, responseId, text)
SELECT id,
       creationTime,
       lastUpdated,
       version,
       questionId,
       responseId,
       DECODE(SUBSTR(text, 10), 'base64')
FROM answer
WHERE answer.questionId IN (
    SELECT id
    FROM openQuestion
)
  AND answer.text IS NOT NULL;

INSERT INTO choiceAnswer (choiceId, responseId)
SELECT checkboxId, responseId
FROM checkboxSelections
LEFT JOIN answer
ON checkboxSelections.answerid = answer.id;

INSERT INTO choiceAnswerText (id, creationTime, lastUpdated, version, questionId, responseId, text)
SELECT id,
       creationTime,
       lastUpdated,
       version,
       questionId,
       responseId,
       DECODE(SUBSTR(text, 10), 'base64')
FROM answer
WHERE answer.questionId IN (
    SELECT id
    FROM choiceQuestion
)
  AND answer.text IS NOT NULL;

DELETE FROM answer
WHERE id IN (SELECT id FROM openAnswer) OR id IN (SELECT id FROM choiceAnswer);

ALTER TABLE answer
    ADD CHECK (FALSE);

DELETE FROM question
WHERE id IN (SELECT id FROM openQuestion) OR id IN (SELECT id FROM choiceQuestion);

ALTER TABLE question
    ADD CHECK (FALSE);

DROP TABLE checkboxSelections, checkbox, checkboxGroup, answer, question;