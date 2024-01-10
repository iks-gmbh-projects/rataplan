ALTER TABLE vote
    ADD COLUMN yesLimitActive boolean NOT NULL
        default false;

ALTER TABLE vote
    ALTER COLUMN yesLimitActive
        DROP DEFAULT;


ALTER TABLE vote
    ADD COLUMN yesAnswerLimit integer
        default null;

ALTER TABLE vote
    ALTER COLUMN yesAnswerLimit
        DROP DEFAULT;

