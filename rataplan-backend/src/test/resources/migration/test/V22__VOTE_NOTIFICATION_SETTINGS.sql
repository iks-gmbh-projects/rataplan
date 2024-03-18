ALTER TABLE vote
    ADD COLUMN recipientEmail bytea NULL;
ALTER TABLE vote
    ADD COLUMN notifyParticipation boolean NULL;
ALTER TABLE vote
    ADD COLUMN notifyExpiration boolean NULL;
ALTER TABLE vote
    ADD CONSTRAINT NOT_USERID_AND_RECIPIENTEMAIL CHECK (userId IS NULL OR recipientEmail IS NULL);
ALTER TABLE vote
    ADD CONSTRAINT NOTIFICATION_SETTINGS_CONSISTENT CHECK (
        (recipientEmail IS NULL AND notifyParticipation IS NULL AND notifyExpiration IS NULL) OR
        (recipientEmail IS NOT NULL AND notifyParticipation IS NOT NULL AND notifyParticipation IS NOT NULL)
        );

ALTER TABLE vote
    DROP COLUMN organizerMail;
