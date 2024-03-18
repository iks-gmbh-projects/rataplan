/* delete backendUserAccess entries for votes that got deleted */
DELETE
FROM backendUserAccess
WHERE voteId IS NULL;

ALTER TABLE backendUserAccess
    ALTER COLUMN voteId SET NOT NULL;
ALTER TABLE backendUserAccess
    DROP CONSTRAINT backendUserAccess_voteId_fkey;
ALTER TABLE backendUserAccess
    ADD FOREIGN KEY (voteId) REFERENCES vote (id) ON DELETE CASCADE;

ALTER TABLE voteDecision
    DROP CONSTRAINT voteDecision_voteOptionId_fkey;
ALTER TABLE voteDecision
    DROP CONSTRAINT voteDecision_voteParticipantId_fkey;
ALTER TABLE voteDecision
    ADD FOREIGN KEY (voteOptionId) REFERENCES voteOption (id) ON DELETE CASCADE;
ALTER TABLE voteDecision
    ADD FOREIGN KEY (voteParticipantId) REFERENCES voteParticipant (id) ON DELETE CASCADE;

/* delete voteOption entries for votes that got deleted */
DELETE
FROM voteOption
WHERE voteId IS NULL;

ALTER TABLE voteOption
    ALTER COLUMN voteId SET NOT NULL;
ALTER TABLE voteOption
    DROP CONSTRAINT voteOption_voteId_fkey;
ALTER TABLE voteOption
    ADD FOREIGN KEY (voteId) REFERENCES vote (id) ON DELETE CASCADE;

/* delete voteParticipant entries for votes that got deleted */
DELETE
FROM voteParticipant
WHERE voteId IS NULL;

ALTER TABLE voteParticipant
    ALTER COLUMN voteId SET NOT NULL;
ALTER TABLE voteParticipant
    DROP CONSTRAINT voteParticipant_voteId_fkey;
ALTER TABLE voteParticipant
    ADD FOREIGN KEY (voteId) REFERENCES vote (id) ON DELETE CASCADE;
