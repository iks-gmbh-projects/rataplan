CREATE TABLE checkboxSelections (
	answerId     BIGINT REFERENCES answer(id) ON DELETE CASCADE,
	checkboxId   BIGINT REFERENCES checkbox(id) ON DELETE CASCADE,
	PRIMARY KEY (answerId, checkboxId)
);

INSERT INTO checkboxSelections (answerId, checkboxId)
SELECT answer.id AS answerId, checkboxId
FROM answer
WHERE checkboxId is not NULL;

ALTER TABLE answer
	DROP COLUMN checkboxId;

UPDATE checkboxSelections
SET answerId = compare.id
FROM answer
INNER JOIN answer AS compare
ON answer.id > compare.id
AND answer.responseId = compare.responseId
WHERE answerId = answer.id;

DELETE FROM answer
USING answer AS compare
WHERE answer.id > compare.id
AND answer.responseId = compare.responseId
AND answer.questionId = compare.questionId;

ALTER TABLE answer
	ADD UNIQUE(responseId, questionId);