ALTER TABLE voteconfig
    ADD COLUMN yeslimitactive boolean
        default false;

ALTER TABLE voteconfig
    ALTER COLUMN yeslimitactive
        DROP DEFAULT;


ALTER TABLE voteconfig
    ADD COLUMN yesanswerlimit integer
        default null;

ALTER TABLE voteconfig
    ALTER COLUMN yesanswerlimit
        DROP DEFAULT;

