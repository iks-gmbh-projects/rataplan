ALTER TABLE survey
    RENAME startdate TO startdate2;
ALTER TABLE survey
	RENAME enddate TO enddate2;
ALTER TABLE survey
	ADD startdate TIMESTAMP WITH TIME ZONE,
	ADD enddate TIMESTAMP WITH TIME ZONE;

UPDATE survey
SET (startdate, enddate) = (startdate2 AT TIME ZONE 'UTC', enddate2 AT TIME ZONE 'UTC');

ALTER TABLE survey
	ALTER COLUMN startdate SET NOT NULL,
	ALTER COLUMN enddate SET NOT NULL,
	DROP startdate2,
	DROP enddate2;