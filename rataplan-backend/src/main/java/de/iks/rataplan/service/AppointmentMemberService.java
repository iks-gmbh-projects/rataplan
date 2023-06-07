package de.iks.rataplan.service;

import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.domain.AppointmentRequest;

public interface AppointmentMemberService {
	public VoteParticipant createAppointmentMember(AppointmentRequest appointmentRequest,
			VoteParticipant voteParticipant
    );

	public void deleteAppointmentMember(AppointmentRequest appointmentRequest, VoteParticipant voteParticipant);

	public VoteParticipant updateAppointmentMember(AppointmentRequest appointmentRequest,
			VoteParticipant dbVoteParticipant, VoteParticipant newVoteParticipant
    );
	
	public void anonymizeAppointmentMember(int memberId);
}
