package de.iks.rataplan.service;

import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.domain.Vote;

public interface VoteParticipantService {
	public VoteParticipant createAppointmentMember(
		Vote vote,
			VoteParticipant voteParticipant
    );

	public void deleteAppointmentMember(Vote vote, VoteParticipant voteParticipant);

	public VoteParticipant updateAppointmentMember(
		Vote vote,
			VoteParticipant dbVoteParticipant, VoteParticipant newVoteParticipant
    );
	
	public void anonymizeAppointmentMember(int memberId);
}
