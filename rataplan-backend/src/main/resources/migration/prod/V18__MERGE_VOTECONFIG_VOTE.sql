ALTER TABLE vote
    ADD COLUMN decisionType  INTEGER,
    ADD COLUMN isStartDate   boolean,
    ADD COLUMN isEndDate     boolean,
    ADD COLUMN isStartTime   boolean,
    ADD COLUMN isEndTime     boolean,
    ADD COLUMN isURL         boolean,
    ADD COLUMN isDescription boolean;

MERGE INTO vote
USING voteConfig
ON vote.voteConfigId = voteConfig.id
WHEN MATCHED THEN
    UPDATE
    SET (
            decisionType,
            isStartDate,
            isEndDate,
            isStartTime,
            isEndTime,
            isURL,
            isDescription
            ) = (
            voteConfig.decisionType,
            voteConfig.isStartDate,
            voteConfig.isEndDate,
            voteConfig.isStartTime,
            voteConfig.isEndTime,
            voteConfig.isURL,
            voteConfig.isDescription
            );

ALTER TABLE vote
    ALTER COLUMN decisionType SET NOT NULL,
    ALTER COLUMN isStartDate SET NOT NULL,
    ALTER COLUMN isEndDate SET NOT NULL,
    ALTER COLUMN isStartTime SET NOT NULL,
    ALTER COLUMN isEndTime SET NOT NULL,
    ALTER COLUMN isURL SET NOT NULL,
    ALTER COLUMN isDescription SET NOT NULL,
    DROP COLUMN voteconfigid;

DROP TABLE voteConfig;