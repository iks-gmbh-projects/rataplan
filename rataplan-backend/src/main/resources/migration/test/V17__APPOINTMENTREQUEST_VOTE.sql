ALTER TABLE appointment
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointment
    RENAME TO voteOption;


ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentId TO voteOptionId;

ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentMemberId TO voteParticipantId;

ALTER TABLE appointmentDecision
    RENAME TO voteDecision;


ALTER TABLE appointmentMember
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointmentMember
    RENAME TO voteParticipant;


ALTER TABLE appointmentRequest
    RENAME COLUMN appointmentRequestConfigId TO voteConfigId;

ALTER TABLE appointmentRequest
    RENAME TO vote;


ALTER TABLE appointmentRequestConfig
    RENAME TO voteConfig;


ALTER TABLE backendUserAccess
    RENAME COLUMN appointmentRequestId TO voteId;