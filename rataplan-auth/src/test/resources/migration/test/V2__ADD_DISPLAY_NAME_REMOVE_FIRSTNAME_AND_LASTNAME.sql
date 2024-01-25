ALTER TABLE rataplanuser
DROP COLUMN firstname;

ALTER TABLE rataplanuser
DROP COLUMN lastname;

ALTER TABLE rataplanuser
ADD displayname varchar(50) NOT NULL DEFAULT '';
