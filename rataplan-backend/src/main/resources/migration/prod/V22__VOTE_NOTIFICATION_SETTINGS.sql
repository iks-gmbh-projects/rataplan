UPDATE vote
SET organizerMail = NULL
WHERE userId IS NOT NULL;

ALTER TABLE vote
    ADD CONSTRAINT ORGANIZERMAIL_ENCRYPTED CHECK (organizerMail IS NULL OR organizerMail LIKE 'ENC\_\_##\_\_%' );

ALTER TABLE vote
    ADD COLUMN recipientEmail      BYTEA   NULL,
    ADD COLUMN notifyParticipation boolean NULL,
    ADD COLUMN notifyExpiration    boolean NULL,
    ADD CONSTRAINT NOT_RECIPIENT_AND_USERID CHECK (recipientEmail IS NULL OR userId IS NULL),
    ADD CONSTRAINT NOTIFICATION_SETTINGS_CONSISTEN CHECK (
        ((recipientEmail IS NULL AND notifyParticipation IS NULL AND notifyExpiration IS NULL) OR
         (recipientEmail IS NOT NULL AND notifyParticipation IS NOT NULL AND notifyExpiration IS NOT NULL))
        );

UPDATE vote
SET recipientEmail      = DECODE(SUBSTR(organizerMail, 9), 'base64'),
    notifyParticipation = FALSE,
    notifyExpiration    = TRUE
WHERE organizermail IS NOT NULL;

ALTER TABLE vote
    DROP CONSTRAINT ORGANIZERMAIL_ENCRYPTED,
    DROP COLUMN organizerMail;
