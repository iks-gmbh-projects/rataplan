package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.VoteParticipant;

import java.util.Collection;

public interface NotificationService {
    void notifyForVoteInvitations(Vote vote);
    void notifyForParticipationInvalidation(Vote vote, Collection<? extends VoteParticipant> affectedParticipants);
    void notifyForVoteCreation(Vote createdVote);
    void notifyForVoteExpired(Vote expiredVote);
}
