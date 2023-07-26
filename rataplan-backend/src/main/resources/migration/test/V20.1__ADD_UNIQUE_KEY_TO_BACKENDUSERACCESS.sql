ALTER TABLE backendUserAccess
    ADD UNIQUE (voteId, userId);