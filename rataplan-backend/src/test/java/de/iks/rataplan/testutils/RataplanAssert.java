package de.iks.rataplan.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.VoteOptionDTO;
import de.iks.rataplan.dto.VoteDecisionDTO;
import de.iks.rataplan.dto.VoteParticipantDTO;
import de.iks.rataplan.dto.AppointmentRequestDTO;

public class RataplanAssert {

	/**
	 * 
	 * @param decision
	 * @param dtoDecision
	 */
	public static void assertVoteDecision(AppointmentDecision decision, VoteDecisionDTO dtoDecision) {
		assertEquals("VoteDecision.Option.Id", decision.getAppointment().getId(),
				dtoDecision.getOptionId());
		assertEquals("VoteDecision.AppointmentMember.Id", decision.getAppointmentMember().getId(),
				dtoDecision.getAppointmentMemberId());
		assertEquals("VoteDecision.Decision", decision.getDecision().getValue(), dtoDecision.getDecision());
		assertEquals("VoteDecision.Participants", decision.getParticipants(), dtoDecision.getParticipants());
	}

	/**
	 * 
	 * @param dtoDecision
	 * @param decision
	 */
	public static void assertVoteDecisionDTO(VoteDecisionDTO dtoDecision, AppointmentDecision decision) {
		assertEquals("VoteDecisionDTO.Option.Id", dtoDecision.getOptionId(),
				decision.getAppointment().getId());
		assertEquals("VoteDecisionDTO.AppointmentMember.Id", dtoDecision.getAppointmentMemberId(),
				decision.getAppointmentMember().getId());
		assertEquals("VoteDecisionDTO.Decision", dtoDecision.getDecision(), decision.getDecision().getValue());
		assertEquals("VoteDecisionDTO.Participants", dtoDecision.getParticipants(), decision.getParticipants());
	}

	/**
	 * 
	 * @param request
	 * @param dtoRequest
	 */
	public static void assertAppointmentRequest(AppointmentRequest request, AppointmentRequestDTO dtoRequest) {
		assertEquals("AppointmentRequest.title", request.getTitle().getString(), dtoRequest.getTitle());
		assertEquals("AppointmentRequest.description", request.getDescription().getString(), dtoRequest.getDescription());
		assertEquals("AppointmentRequest.deadline", request.getDeadline(), dtoRequest.getDeadline());
		assertEquals("AppointmentRequest.organizerMail", request.getOrganizerMail().getString(), dtoRequest.getOrganizerMail());
		assertEquals("AppointmentRequest.id", request.getId(), dtoRequest.getId());
		assertEquals("AppointmentRequest.config.appointmentType", request.getAppointmentRequestConfig().getAppointmentConfig(),
				dtoRequest.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("AppointmentRequest.config.decisionType", request.getAppointmentRequestConfig().getDecisionType(),
				dtoRequest.getAppointmentRequestConfig().getDecisionType());
		assertEquals("AppointmentRequest.options.size", request.getAppointments().size(),
				dtoRequest.getOptions().size());
		assertEquals("AppointmentRequest.participants.size", request.getAppointmentMembers().size(),
				dtoRequest.getParticipants().size());
	}

	/**
	 * 
	 * @param dtoRequest
	 * @param request
	 */
	public static void assertAppointmentRequestDTO(AppointmentRequestDTO dtoRequest, AppointmentRequest request) {
		assertEquals("AppointmentRequestDTO.title", dtoRequest.getTitle(), request.getTitle().getString());
		assertEquals("AppointmentRequestDTO.description", dtoRequest.getDescription(), request.getDescription().getString());
		assertEquals("AppointmentRequestDTO.deadline", dtoRequest.getDeadline(), request.getDeadline());
		assertEquals("AppointmentRequestDTO.organizerMail", dtoRequest.getOrganizerMail(), request.getOrganizerMail().getString());
		assertEquals("AppointmentRequestDTO.id", dtoRequest.getId(), request.getId());
		assertEquals("AppointmentRequestDTO.config.appointmentType", dtoRequest.getAppointmentRequestConfig().getAppointmentConfig(),
				request.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("AppointmentRequestDTO.config.decisionType", dtoRequest.getAppointmentRequestConfig().getDecisionType(),
				request.getAppointmentRequestConfig().getDecisionType());
		if(dtoRequest.getOptions() == null) assertNull("AppointmentRequestDTO.appointments", request.getAppointments());
		else assertEquals("AppointmentRequestDTO.options.size", dtoRequest.getOptions().size(),
				request.getAppointments().size());
		if(dtoRequest.getParticipants() == null) assertNull("AppointmentRequestDTO.participants", request.getAppointmentMembers());
		else assertEquals("AppointmentRequestDTO.participants.size", dtoRequest.getParticipants().size(),
				request.getAppointmentMembers().size());
	}

	/**
	 * 
	 * @param appointment
	 * @param dtoAppointment
	 */
	public static void assertVoteOption(Appointment appointment, VoteOptionDTO dtoAppointment) {
		assertEquals("VoteOption.Id", appointment.getId(), dtoAppointment.getId());
		assertEquals("VoteOption.StartDate", appointment.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("VoteOption.EndDate", appointment.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("VoteOption.Location", appointment.getDescription().getString(), dtoAppointment.getDescription());
		assertEquals("VoteOption.AppointmentRequest.Id", appointment.getAppointmentRequest().getId(),
				dtoAppointment.getRequestId());
	}

	/**
	 * 
	 * @param dtoAppointment
	 * @param appointment
	 */
	public static void assertVoteOptionDTO(VoteOptionDTO dtoAppointment, Appointment appointment) {
		assertEquals("VoteOptionDTO.Id", dtoAppointment.getId(), appointment.getId());
		assertEquals("VoteOption.StartDate", appointment.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("VoteOption.EndDate", appointment.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("VoteOption.Location", dtoAppointment.getDescription(), appointment.getDescription().getString());
		assertEquals("VoteOptionDTO.AppointmentRequestId", dtoAppointment.getRequestId(),
				appointment.getAppointmentRequest().getId());
	}

	/**
	 * 
	 * @param member
	 * @param dtoMember
	 */
	public static void assertVoteParticipant(AppointmentMember member, VoteParticipantDTO dtoMember) {
		assertEquals("VoteParticipant.Id", member.getId(), dtoMember.getId());
		assertEquals("VoteParticipant.Name", member.getName().getString(), dtoMember.getName());
		assertEquals("VoteParticipant.AppointmentRequest.Id", member.getAppointmentRequest().getId(),
				dtoMember.getAppointmentRequestId());
		assertEquals("VoteParticipant.decisions.size", member.getAppointmentDecisions().size(),
				dtoMember.getDecisions().size());
	}

	/**
	 * 
	 * @param dtoMember
	 * @param member
	 */
	public static void assertVoteParticipantDTO(VoteParticipantDTO dtoMember, AppointmentMember member) {
		assertEquals("VoteParticipantDTO.Id", dtoMember.getId(), member.getId());
		assertEquals("VoteParticipantDTO.Name", dtoMember.getName(), member.getName().getString());
		assertEquals("VoteParticipantDTO.AppointmentRequestId", dtoMember.getAppointmentRequestId(),
				member.getAppointmentRequest().getId());
		assertEquals("VoteParticipantDTO.decisions.size", dtoMember.getDecisions().size(),
				member.getAppointmentDecisions().size());
	}
}
