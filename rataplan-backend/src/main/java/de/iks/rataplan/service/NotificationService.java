package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;

import java.util.Collection;

public interface NotificationService {
    void notifyForVoteInvitations(Vote vote);
    void notifyForParticipationInvalidation(Vote vote, Collection<Integer> affectedParticipants);
}
