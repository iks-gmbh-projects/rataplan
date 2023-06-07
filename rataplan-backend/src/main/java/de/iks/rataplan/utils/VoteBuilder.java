package de.iks.rataplan.utils;

import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.domain.VoteParticipant;

public class VoteBuilder {

	private VoteBuilder() {
		// nothing to do here
	}

	public static List<VoteOption> voteOptionList(VoteOption... voteOptions) {
		List<VoteOption> voteOptionList = new ArrayList<>();
		for (VoteOption voteOption : voteOptions) {
			voteOptionList.add(voteOption);
		}
		return voteOptionList;
	}

	public static List<VoteParticipant> voteParticipantList(VoteParticipant... voteParticipants) {
		List<VoteParticipant> voteParticipantList = new ArrayList<>();
		for (VoteParticipant voteParticipant : voteParticipants) {
			voteParticipantList.add(voteParticipant);
		}
		return voteParticipantList;
	}
}
