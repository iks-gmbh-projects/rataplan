ALTER TABLE rataplanuser
    ADD COLUMN accountconfirmed
        boolean NOT NULL DEFAULT True;

ALTER TABLE rataplanuser
    ALTER COLUMN accountconfirmed
        DROP DEFAULT;