package de.iks.rataplan.service;

import java.util.List;

import de.iks.rataplan.domain.Vote;

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
}

