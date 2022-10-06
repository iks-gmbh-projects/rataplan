CREATE TABLE surveyresponse
(
	id              SERIAL PRIMARY KEY,
	creationTime    TIMESTAMP   NOT NULL,
	lastUpdated     TIMESTAMP   NOT NULL,
	version         INT         NOT NULL,
	surveyId        BIGINT REFERENCES survey (id),
	userId          BIGINT NULL
);

INSERT INTO surveyresponse (surveyId, userId, creationtime, lastUpdated, version)
SELECT DISTINCT questionGroup.surveyId, answer.userId, answer.creationTime AS creationTime, answer.lastUpdated AS lastUpdated, 2 AS version
FROM answer
INNER JOIN question ON answer.questionId = question.id
INNER JOIN questionGroup ON question.questionGroupId = questionGroup.id;

ALTER TABLE answer
	ADD COLUMN responseId BIGINT NULL REFERENCES surveyresponse (id);

UPDATE answer
SET responseId = subquery.responseId
FROM (
	SELECT surveyresponse.id AS responseId, surveyresponse.userId, question.id AS questionId
	FROM surveyresponse
	INNER JOIN questionGroup ON surveyresponse.surveyId = questionGroup.surveyId
	INNER JOIN question ON questionGroup.id = question.questionGroupId
) AS subquery
WHERE answer.questionId = subquery.questionId
AND answer.userId = subquery.userId;

DELETE FROM answer
WHERE responseId is NULL;

ALTER TABLE answer
	ALTER COLUMN responseId SET NOT NULL,
	DROP COLUMN userId;

