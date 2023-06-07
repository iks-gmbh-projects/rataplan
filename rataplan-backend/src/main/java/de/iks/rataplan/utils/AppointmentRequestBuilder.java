package de.iks.rataplan.utils;

import java.util.ArrayList;
import java.util.List;

import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.domain.VoteParticipant;

public class AppointmentRequestBuilder {

	private AppointmentRequestBuilder() {
		// nothing to do here
	}

	public static List<VoteOption> appointmentList(VoteOption... voteOptions) {
		List<VoteOption> voteOptionList = new ArrayList<>();
		for (VoteOption voteOption : voteOptions) {
			voteOptionList.add(voteOption);
		}
		return voteOptionList;
	}

	public static List<VoteParticipant> memberList(VoteParticipant... voteParticipants) {
		List<VoteParticipant> voteParticipantList = new ArrayList<>();
		for (VoteParticipant voteParticipant : voteParticipants) {
			voteParticipantList.add(voteParticipant);
		}
		return voteParticipantList;
	}
}
