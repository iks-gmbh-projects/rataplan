ALTER TABLE survey
    ADD COLUMN anonymousParticipation BOOLEAN NULL;

UPDATE survey
SET anonymousParticipation = FALSE
WHERE survey.anonymousParticipation IS NULL;

ALTER TABLE survey
    ALTER COLUMN anonymousParticipation SET NOT NULL;
