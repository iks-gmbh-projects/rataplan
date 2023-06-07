package de.iks.rataplan.service;

import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.domain.Vote;

public interface VoteParticipantService {
	public VoteParticipant createParticipant(
		Vote vote,
			VoteParticipant voteParticipant
    );

	public void deleteParticipant(Vote vote, VoteParticipant voteParticipant);

	public VoteParticipant updateParticipant(
		Vote vote,
			VoteParticipant dbVoteParticipant, VoteParticipant newVoteParticipant
    );
	
	public void anonymizeParticipant(int memberId);
}
