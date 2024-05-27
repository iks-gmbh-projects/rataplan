CREATE TABLE checkboxSelections (
	answerId     BIGINT REFERENCES answer(id) ON DELETE CASCADE,
	checkboxId   BIGINT REFERENCES checkbox(id) ON DELETE CASCADE,
	PRIMARY KEY (answerId, checkboxId)
);

ALTER TABLE answer
	DROP COLUMN checkboxId;

ALTER TABLE answer
	ADD UNIQUE(responseId, questionId);