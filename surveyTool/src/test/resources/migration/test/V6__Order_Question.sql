CREATE TABLE orderQuestion (
    id bigserial NOT NULL PRIMARY KEY,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with time zone NOT NULL DEFAULT NOW(),
    version int NOT NULL,
    questionGroupId bigint NOT NULL REFERENCES questionGroup(id) ON DELETE CASCADE,
    rank int NOT NULL,
    text bytea NOT NULL
);

CREATE TABLE orderQuestionChoice (
    id bigserial NOT NULL PRIMARY KEY,
    creationTime timestamp with time zone NOT NULL DEFAULT NOW(),
    lastUpdated timestamp with time zone NOT NULL DEFAULT NOW(),
    version int NOT NULL,
    questionId bigint NOT NULL REFERENCES orderQuestion(id) ON DELETE CASCADE,
    text bytea NOT NULL
);

CREATE TABLE orderAnswer (
    choiceId bigint NOT NULL REFERENCES orderQuestionChoice(id) ON DELETE CASCADE,
    responseId bigint NOT NULL REFERENCES surveyResponse(id) ON DELETE CASCADE,
    rank int NOT NULL
);