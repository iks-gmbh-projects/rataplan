ALTER TABLE vote
    ADD COLUMN yeslimitactive boolean NOT NULL
        default false;

ALTER TABLE vote
    ALTER COLUMN yeslimitactive
        DROP DEFAULT;


ALTER TABLE vote
    ADD COLUMN yesanswerlimit integer
        default null;

ALTER TABLE vote
    ALTER COLUMN yesanswerlimit
        DROP DEFAULT;