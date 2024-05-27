CREATE TABLE surveyresponse
(
	id              BIGINT PRIMARY KEY AUTO_INCREMENT,
	creationTime    TIMESTAMP   NOT NULL,
	lastUpdated     TIMESTAMP   NOT NULL,
	version         INT         NOT NULL,
	surveyId        BIGINT      NOT NULL,
	userId          BIGINT NULL,
    CONSTRAINT surveyresponse_surveyId_fkey FOREIGN KEY (surveyId) REFERENCES survey(id)
);

ALTER TABLE answer
	ADD COLUMN responseId BIGINT NOT NULL;
ALTER TABLE answer
    ADD CONSTRAINT answer_responseId_fkey FOREIGN KEY (responseId) REFERENCES surveyresponse (id);

Alter TABLE answer
	DROP COLUMN userId;