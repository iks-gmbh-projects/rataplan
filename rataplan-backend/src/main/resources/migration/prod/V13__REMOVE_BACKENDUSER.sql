ALTER TABLE appointmentmember
DROP CONSTRAINT backendUserId;

ALTER TABLE appointmentmember
RENAME COLUMN backenduserid TO userid;

INSERT INTO appointmentmember (userid)
VALUES

ALTER TABLE appointmentrequest
DROP CONSTRAINT backendUserId;

ALTER TABLE appointmentrequest
RENAME COLUMN backenduserid TO userid;

ALTER TABLE backenduseraccess
DROP CONSTRAINT backendUserId;

ALTER TABLE backenduseraccess
RENAME COLUMN backenduserid TO userid;

