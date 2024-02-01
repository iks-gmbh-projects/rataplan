ALTER TABLE rataplanuser
    ADD COLUMN mail2 bytea,
    ADD COLUMN username2 bytea,
    ADD COLUMN displayname2 bytea;

UPDATE rataplanuser
SET mail2 = decode(mail, 'base64'),
    username2 = decode(username, 'base64'),
    displayname2 = decode(displayname, 'base64')
WHERE encrypted;

ALTER TABLE rataplanuser
    ALTER COLUMN mail2 SET NOT NULL,
    ALTER COLUMN username2 SET NOT NULL,
    ALTER COLUMN displayname2 SET NOT NULL,
    DROP COLUMN mail,
    DROP COLUMN username,
    DROP COLUMN displayname,
    DROP COLUMN encrypted;

ALTER TABLE rataplanuser
    RENAME COLUMN mail2 TO mail;
ALTER TABLE rataplanuser
    RENAME COLUMN username2 TO username;
ALTER TABLE rataplanuser
    RENAME COLUMN displayname2 TO displayname;