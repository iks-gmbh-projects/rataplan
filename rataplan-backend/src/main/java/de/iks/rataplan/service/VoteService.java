package de.iks.rataplan.service;

import de.iks.rataplan.domain.BackendUserAccess;
import de.iks.rataplan.domain.Vote;

import java.util.Collection;
import java.util.List;

public interface VoteService {
    public List<Vote> getVotes();
    public List<Vote> getVotesForUser(Integer userId);
    public Vote getVoteById(Integer requestId);
    public Vote getVoteByParticipationToken(String participationToken);
    public Vote getVoteByEditToken(String editToken);
    public Vote createVote(Vote vote);
    public Vote updateVote(Vote dbVote, Vote newVote);
    public List<Vote> getVotesWhereUserParticipates(Integer userId);
    public void deleteVote(Vote request);
    public void anonymizeVotes(Integer userId);
    Vote addAccess(Vote vote, Collection<? extends BackendUserAccess> backendUserAccesses);
}

