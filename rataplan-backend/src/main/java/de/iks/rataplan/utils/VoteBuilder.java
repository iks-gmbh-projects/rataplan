package de.iks.rataplan.utils;

import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.domain.VoteParticipant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoteBuilder {
	public static List<VoteOption> voteOptionList(VoteOption... voteOptions) {
		return new ArrayList<>(Arrays.asList(voteOptions));
	}

	public static List<VoteParticipant> voteParticipantList(VoteParticipant... voteParticipants) {
		return new ArrayList<>(Arrays.asList(voteParticipants));
	}
}
