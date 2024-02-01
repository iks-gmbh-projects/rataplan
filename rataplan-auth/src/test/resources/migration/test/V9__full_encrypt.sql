ALTER TABLE rataplanuser
    ALTER COLUMN mail SET DATA TYPE bytea;
ALTER TABLE rataplanuser
    ALTER COLUMN username SET DATA TYPE bytea;
ALTER TABLE rataplanuser
    ALTER COLUMN displayname SET DATA TYPE bytea;
ALTER TABLE rataplanuser
    DROP COLUMN encrypted;