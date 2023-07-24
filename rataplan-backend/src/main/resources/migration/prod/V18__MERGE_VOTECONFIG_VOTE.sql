ALTER TABLE vote
    ADD COLUMN decisionType  INTEGER,
    ADD COLUMN isStartDate   boolean,
    ADD COLUMN isEndDate     boolean,
    ADD COLUMN isStartTime   boolean,
    ADD COLUMN isEndTime     boolean,
    ADD COLUMN isURL         boolean,
    ADD COLUMN isDescription boolean;

UPDATE vote
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
            )
FROM voteConfig
WHERE vote.voteConfigId = voteConfig.id;

ALTER TABLE vote
    ALTER COLUMN decisionType SET NOT NULL,
    ALTER COLUMN isStartDate SET NOT NULL,
    ALTER COLUMN isEndDate SET NOT NULL,
    ALTER COLUMN isStartTime SET NOT NULL,
    ALTER COLUMN isEndTime SET NOT NULL,
    ALTER COLUMN isURL SET NOT NULL,
    ALTER COLUMN isDescription SET NOT NULL,
    DROP COLUMN voteConfigId;

DROP TABLE voteConfig;