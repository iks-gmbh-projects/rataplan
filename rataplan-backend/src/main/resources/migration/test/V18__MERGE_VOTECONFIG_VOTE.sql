ALTER TABLE vote
    ADD COLUMN decisionType  INTEGER;
ALTER TABLE vote
    ADD COLUMN isStartDate   boolean;
ALTER TABLE vote
    ADD COLUMN isEndDate     boolean;
ALTER TABLE vote
    ADD COLUMN isStartTime   boolean;
ALTER TABLE vote
    ADD COLUMN isEndTime     boolean;
ALTER TABLE vote
    ADD COLUMN isURL         boolean;
ALTER TABLE vote
    ADD COLUMN isDescription boolean;

ALTER TABLE vote
    ALTER COLUMN decisionType SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isStartDate SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isEndDate SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isStartTime SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isEndTime SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isURL SET NOT NULL;
ALTER TABLE vote
    ALTER COLUMN isDescription SET NOT NULL;
ALTER TABLE vote
    DROP COLUMN voteconfigid;

DROP TABLE voteConfig;