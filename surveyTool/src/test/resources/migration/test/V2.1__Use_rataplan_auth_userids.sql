ALTER TABLE survey
	DROP CONSTRAINT survey_userid_fkey;

DROP TABLE surveytooluser;

UPDATE survey
SET userid = NULL;

UPDATE surveyresponse
SET userid = NULL;