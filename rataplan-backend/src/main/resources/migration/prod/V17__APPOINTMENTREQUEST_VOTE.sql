ALTER TABLE appointment
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointment
    RENAME CONSTRAINT appointment_pkey TO voteOption_pkey;

ALTER TABLE appointment
    RENAME CONSTRAINT appointment_appointmentrequestid_fkey TO voteOption_voteId_fkey;

ALTER TABLE appointment
    RENAME TO voteOption;

ALTER SEQUENCE appointment_id_seq
    RENAME TO voteOption_id_seq;


ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentId TO voteOptionId;

ALTER TABLE appointmentDecision
    RENAME COLUMN appointmentMemberId TO voteParticipantId;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentdecision_pkey TO voteDecision_pkey;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentdecision_appointmentmemberid_fkey TO voteDecision_voteParticipantId_fkey;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentdecision_appointmentid_fkey TO voteDecision_voteOptionId_fkey;

ALTER TABLE appointmentDecision
    RENAME CONSTRAINT appointmentdecision_appointmentid_appointmentmemberid_key TO voteDecision_voteId_voteParticipantId_key;

ALTER TABLE appointmentDecision
    RENAME TO voteDecision;


ALTER TABLE appointmentMember
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE appointmentMember
    RENAME CONSTRAINT appointmentMember_pkey TO voteParticipant_pkey;

ALTER TABLE appointmentMember
    RENAME CONSTRAINT appointmentMember_appointmentRequestId_fkey TO voteParticipant_voteId_fkey;

ALTER TABLE appointmentMember
    RENAME TO voteParticipant;

ALTER SEQUENCE appointmentMember_id_seq
    RENAME TO voteParticipant_id_seq;


ALTER TABLE appointmentRequest
    RENAME COLUMN appointmentRequestConfigId TO voteConfigId;

ALTER TABLE appointmentRequest
    RENAME CONSTRAINT appointmentRequest_pkey TO vote_pkey;

ALTER TABLE appointmentRequest
    RENAME CONSTRAINT appointmentRequest_participationToken TO vote_participationToken_key;

ALTER TABLE appointmentRequest
    RENAME CONSTRAINT appointmentRequest_editToken_key TO vote_editToken_key;

ALTER TABLE appointmentRequest
    RENAME CONSTRAINT appointmentRequest_appointmentRequestConfigId_fkey TO vote_voteConfigId_fkey;

ALTER TABLE appointmentRequest
    RENAME TO vote;

ALTER SEQUENCE appointmentRequest_id_seq
    RENAME TO vote_id_seq;


ALTER TABLE appointmentRequestConfig
    RENAME CONSTRAINT appointmentRequestConfig_pkey TO voteConfig_pkey;

ALTER TABLE appointmentRequestConfig
    RENAME TO voteConfig;

ALTER SEQUENCE appointmentRequestConfig_id_seq
    RENAME TO voteConfig_id_seq;


ALTER TABLE backendUserAccess
    RENAME COLUMN appointmentRequestId TO voteId;

ALTER TABLE backendUserAccess
    RENAME CONSTRAINT backendUserAccess_appointmentRequestId_fkey TO backendUserAccess_voteId_fkey;