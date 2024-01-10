ALTER TABLE appointment
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointment
    RENAME CONSTRAINT appointment_appointmentRequestId_fkey TO voteOption_voteId_fkey;

ALTER TABLE appointment
    RENAME TO voteOption;


ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentId TO voteOptionId;

ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentMemberId TO voteParticipantId;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentDecision_appointmentId_fkey TO voteDecision_voteOptionId_fkey;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentDecision_appointmentMemberId_fkey TO voteDecision_voteParticipantId_fkey;

ALTER TABLE appointmentDecision
    RENAME TO voteDecision;


ALTER TABLE appointmentMember
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointmentMember
    RENAME CONSTRAINT appointmentMember_appointmentRequestId_fkey TO voteParticipant_voteId_fkey;

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
ALTER TABLE backendUserAccess
    RENAME CONSTRAINT backendUserAccess_appointmentRequestId_fkey TO backendUserAccess_voteId_fkey;