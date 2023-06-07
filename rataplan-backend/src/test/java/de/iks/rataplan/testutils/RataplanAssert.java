package de.iks.rataplan.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.iks.rataplan.domain.VoteOption;
import de.iks.rataplan.domain.VoteDecision;
import de.iks.rataplan.domain.VoteParticipant;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.VoteOptionDTO;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.dto.CreatorVoteDTO;

public class RataplanAssert {

	/**
	 * 
	 * @param decision
	 * @param dtoDecision
	 */
	public static void assertVoteDecision(VoteDecision decision, VoteDecisionDTO dtoDecision) {
		assertEquals("VoteDecision.Option.Id", decision.getAppointment().getId(),
				dtoDecision.getOptionId());
		assertEquals("VoteDecision.participant.Id", decision.getAppointmentMember().getId(),
				dtoDecision.getParticipantId());
		assertEquals("VoteDecision.Decision", decision.getDecision().getValue(), dtoDecision.getDecision());
		assertEquals("VoteDecision.Participants", decision.getParticipants(), dtoDecision.getParticipants());
	}

	/**
	 * 
	 * @param dtoDecision
	 * @param decision
	 */
	public static void assertVoteDecisionDTO(VoteDecisionDTO dtoDecision, VoteDecision decision) {
		assertEquals("VoteDecisionDTO.Option.Id", dtoDecision.getOptionId(),
				decision.getAppointment().getId());
		assertEquals("VoteDecisionDTO.participant.Id", dtoDecision.getParticipantId(),
				decision.getAppointmentMember().getId());
		assertEquals("VoteDecisionDTO.Decision", dtoDecision.getDecision(), decision.getDecision().getValue());
		assertEquals("VoteDecisionDTO.Participants", dtoDecision.getParticipants(), decision.getParticipants());
	}

	/**
	 * 
	 * @param request
	 * @param dtoRequest
	 */
	public static void assertVote(AppointmentRequest request, CreatorVoteDTO dtoRequest) {
		assertEquals("Vote.title", request.getTitle().getString(), dtoRequest.getTitle());
		assertEquals("Vote.description", request.getDescription().getString(), dtoRequest.getDescription());
		assertEquals("Vote.deadline", request.getDeadline(), dtoRequest.getDeadline());
		assertEquals("Vote.organizerMail", request.getOrganizerMail().getString(), dtoRequest.getOrganizerMail());
		assertEquals("Vote.id", request.getId(), dtoRequest.getId());
		assertEquals("Vote.config.appointmentType", request.getAppointmentRequestConfig().getAppointmentConfig(),
				dtoRequest.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("Vote.config.decisionType", request.getAppointmentRequestConfig().getDecisionType(),
				dtoRequest.getAppointmentRequestConfig().getDecisionType());
		assertEquals("Vote.options.size", request.getAppointments().size(),
				dtoRequest.getOptions().size());
		assertEquals("Vote.participants.size", request.getAppointmentMembers().size(),
				dtoRequest.getParticipants().size());
	}

	/**
	 * 
	 * @param dtoRequest
	 * @param request
	 */
	public static void assertVoteDTO(CreatorVoteDTO dtoRequest, AppointmentRequest request) {
		assertEquals("VoteDTO.title", dtoRequest.getTitle(), request.getTitle().getString());
		assertEquals("VoteDTO.description", dtoRequest.getDescription(), request.getDescription().getString());
		assertEquals("VoteDTO.deadline", dtoRequest.getDeadline(), request.getDeadline());
		assertEquals("VoteDTO.organizerMail", dtoRequest.getOrganizerMail(), request.getOrganizerMail().getString());
		assertEquals("VoteDTO.id", dtoRequest.getId(), request.getId());
		assertEquals("VoteDTO.config.appointmentType", dtoRequest.getAppointmentRequestConfig().getAppointmentConfig(),
				request.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("VoteDTO.config.decisionType", dtoRequest.getAppointmentRequestConfig().getDecisionType(),
				request.getAppointmentRequestConfig().getDecisionType());
		if(dtoRequest.getOptions() == null) assertNull("VoteDTO.appointments", request.getAppointments());
		else assertEquals("VoteDTO.options.size", dtoRequest.getOptions().size(),
				request.getAppointments().size());
		if(dtoRequest.getParticipants() == null) assertNull("VoteDTO.participants", request.getAppointmentMembers());
		else assertEquals("VoteDTO.participants.size", dtoRequest.getParticipants().size(),
				request.getAppointmentMembers().size());
	}

	/**
	 * 
	 * @param voteOption
	 * @param dtoAppointment
	 */
	public static void assertVoteOption(VoteOption voteOption, VoteOptionDTO dtoAppointment) {
		assertEquals("VoteOption.Id", voteOption.getId(), dtoAppointment.getId());
		assertEquals("VoteOption.StartDate", voteOption.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("VoteOption.EndDate", voteOption.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("VoteOption.Location", voteOption.getDescription().getString(), dtoAppointment.getDescription());
		assertEquals("VoteOption.Vote.Id", voteOption.getAppointmentRequest().getId(),
				dtoAppointment.getVoteId());
	}

	/**
	 * 
	 * @param dtoAppointment
	 * @param voteOption
	 */
	public static void assertVoteOptionDTO(VoteOptionDTO dtoAppointment, VoteOption voteOption) {
		assertEquals("VoteOptionDTO.Id", dtoAppointment.getId(), voteOption.getId());
		assertEquals("VoteOption.StartDate", voteOption.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("VoteOption.EndDate", voteOption.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("VoteOption.Location", dtoAppointment.getDescription(), voteOption.getDescription().getString());
		assertEquals("VoteOptionDTO.VoteId", dtoAppointment.getVoteId(),
				voteOption.getAppointmentRequest().getId());
	}

	/**
	 * 
	 * @param member
	 * @param dtoMember
	 */
	public static void assertVoteParticipant(VoteParticipant member, VoteParticipantDTO dtoMember) {
		assertEquals("VoteParticipant.Id", member.getId(), dtoMember.getId());
		assertEquals("VoteParticipant.Name", member.getName().getString(), dtoMember.getName());
		assertEquals("VoteParticipant.Vote.Id", member.getAppointmentRequest().getId(),
				dtoMember.getVoteId());
		assertEquals("VoteParticipant.decisions.size", member.getAppointmentDecisions().size(),
				dtoMember.getDecisions().size());
	}

	/**
	 * 
	 * @param dtoMember
	 * @param member
	 */
	public static void assertVoteParticipantDTO(VoteParticipantDTO dtoMember, VoteParticipant member) {
		assertEquals("VoteParticipantDTO.Id", dtoMember.getId(), member.getId());
		assertEquals("VoteParticipantDTO.Name", dtoMember.getName(), member.getName().getString());
		assertEquals("VoteParticipantDTO.VoteId", dtoMember.getVoteId(),
				member.getAppointmentRequest().getId());
		assertEquals("VoteParticipantDTO.decisions.size", dtoMember.getDecisions().size(),
				member.getAppointmentDecisions().size());
	}
}
