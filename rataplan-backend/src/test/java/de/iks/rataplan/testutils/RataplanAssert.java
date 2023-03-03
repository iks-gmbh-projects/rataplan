package de.iks.rataplan.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import de.iks.rataplan.domain.Appointment;
import de.iks.rataplan.domain.AppointmentDecision;
import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.dto.AppointmentDTO;
import de.iks.rataplan.dto.AppointmentDecisionDTO;
import de.iks.rataplan.dto.AppointmentMemberDTO;
import de.iks.rataplan.dto.AppointmentRequestDTO;

public class RataplanAssert {

	/**
	 * 
	 * @param decision
	 * @param dtoDecision
	 */
	public static void assertAppointmentDecision(AppointmentDecision decision, AppointmentDecisionDTO dtoDecision) {
		assertEquals("AppointmentDecision.Appointment.Id", decision.getAppointment().getId(),
				dtoDecision.getAppointmentId());
		assertEquals("AppointmentDecision.AppointmentMember.Id", decision.getAppointmentMember().getId(),
				dtoDecision.getAppointmentMemberId());
		assertEquals("AppointmentDecision.Decision", decision.getDecision().getValue(), dtoDecision.getDecision());
		assertEquals("AppointmentDecision.Participants", decision.getParticipants(), dtoDecision.getParticipants());
	}

	/**
	 * 
	 * @param dtoDecision
	 * @param decision
	 */
	public static void assertAppointmentDecisionDTO(AppointmentDecisionDTO dtoDecision, AppointmentDecision decision) {
		assertEquals("AppointmentDecisionDTO.Appointment.Id", dtoDecision.getAppointmentId(),
				decision.getAppointment().getId());
		assertEquals("AppointmentDecisionDTO.AppointmentMember.Id", dtoDecision.getAppointmentMemberId(),
				decision.getAppointmentMember().getId());
		assertEquals("AppointmentDecisionDTO.Decision", dtoDecision.getDecision(), decision.getDecision().getValue());
		assertEquals("AppointmentDecisionDTO.Participants", dtoDecision.getParticipants(), decision.getParticipants());
	}

	/**
	 * 
	 * @param request
	 * @param dtoRequest
	 */
	public static void assertAppointmentRequest(AppointmentRequest request, AppointmentRequestDTO dtoRequest) {
		assertEquals("AppointmentRequest.title", request.getTitle(), dtoRequest.getTitle());
		assertEquals("AppointmentRequest.description", request.getDescription(), dtoRequest.getDescription());
		assertEquals("AppointmentRequest.deadline", request.getDeadline(), dtoRequest.getDeadline());
		assertEquals("AppointmentRequest.organizerMail", request.getOrganizerMail(), dtoRequest.getOrganizerMail());
		assertEquals("AppointmentRequest.id", request.getId(), dtoRequest.getId());
		assertEquals("AppointmentRequest.config.appointmentType", request.getAppointmentRequestConfig().getAppointmentConfig(),
				dtoRequest.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("AppointmentRequest.config.decisionType", request.getAppointmentRequestConfig().getDecisionType(),
				dtoRequest.getAppointmentRequestConfig().getDecisionType());
		assertEquals("AppointmentRequest.appointments.size", request.getAppointments().size(),
				dtoRequest.getAppointments().size());
		assertEquals("AppointmentRequest.appointmentMembers.size", request.getAppointmentMembers().size(),
				dtoRequest.getAppointmentMembers().size());
	}

	/**
	 * 
	 * @param dtoRequest
	 * @param request
	 */
	public static void assertAppointmentRequestDTO(AppointmentRequestDTO dtoRequest, AppointmentRequest request) {
		assertEquals("AppointmentRequestDTO.title", dtoRequest.getTitle(), request.getTitle());
		assertEquals("AppointmentRequestDTO.description", dtoRequest.getDescription(), request.getDescription());
		assertEquals("AppointmentRequestDTO.deadline", dtoRequest.getDeadline(), request.getDeadline());
		assertEquals("AppointmentRequestDTO.organizerMail", dtoRequest.getOrganizerMail(), request.getOrganizerMail());
		assertEquals("AppointmentRequestDTO.id", dtoRequest.getId(), request.getId());
		assertEquals("AppointmentRequestDTO.config.appointmentType", dtoRequest.getAppointmentRequestConfig().getAppointmentConfig(),
				request.getAppointmentRequestConfig().getAppointmentConfig());
		assertEquals("AppointmentRequestDTO.config.decisionType", dtoRequest.getAppointmentRequestConfig().getDecisionType(),
				request.getAppointmentRequestConfig().getDecisionType());
		if(dtoRequest.getAppointments() == null) assertNull("AppointmentRequestDTO.appointments", request.getAppointments());
		else assertEquals("AppointmentRequestDTO.appointments.size", dtoRequest.getAppointments().size(),
				request.getAppointments().size());
		if(dtoRequest.getAppointmentMembers() == null) assertNull("AppointmentRequestDTO.appointmentMembers", request.getAppointmentMembers());
		else assertEquals("AppointmentRequestDTO.appointmentMembers.size", dtoRequest.getAppointmentMembers().size(),
				request.getAppointmentMembers().size());
	}

	/**
	 * 
	 * @param appointment
	 * @param dtoAppointment
	 */
	public static void assertAppointment(Appointment appointment, AppointmentDTO dtoAppointment) {
		assertEquals("Appointment.Id", appointment.getId(), dtoAppointment.getId());
		assertEquals("Appointment.StartDate", appointment.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("Appointment.EndDate", appointment.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("Appointment.Location", appointment.getDescription(), dtoAppointment.getDescription());
		assertEquals("Appointment.AppointmentRequest.Id", appointment.getAppointmentRequest().getId(),
				dtoAppointment.getRequestId());
	}

	/**
	 * 
	 * @param dtoAppointment
	 * @param appointment
	 */
	public static void assertAppointmentDTO(AppointmentDTO dtoAppointment, Appointment appointment) {
		assertEquals("AppointmentDTO.Id", dtoAppointment.getId(), appointment.getId());
		assertEquals("Appointment.StartDate", appointment.getStartDate(), dtoAppointment.getStartDate());
		assertEquals("Appointment.EndDate", appointment.getEndDate(), dtoAppointment.getEndDate());
		assertEquals("AppointmentDTO.Location", dtoAppointment.getDescription(), appointment.getDescription());
		assertEquals("AppointmentDTO.AppointmentRequestId", dtoAppointment.getRequestId(),
				appointment.getAppointmentRequest().getId());
	}

	/**
	 * 
	 * @param member
	 * @param dtoMember
	 */
	public static void assertAppointmentMember(AppointmentMember member, AppointmentMemberDTO dtoMember) {
		assertEquals("AppointmentMember.Id", member.getId(), dtoMember.getId());
		assertEquals("AppointmentMember.Name", member.getName(), dtoMember.getName());
		assertEquals("AppointmentMember.AppointmentRequest.Id", member.getAppointmentRequest().getId(),
				dtoMember.getAppointmentRequestId());
		assertEquals("AppointmentMember.AppointmentDecisions.size", member.getAppointmentDecisions().size(),
				dtoMember.getAppointmentDecisions().size());
	}

	/**
	 * 
	 * @param dtoMember
	 * @param member
	 */
	public static void assertAppointmentMemberDTO(AppointmentMemberDTO dtoMember, AppointmentMember member) {
		assertEquals("AppointmentMemberDTO.Id", dtoMember.getId(), member.getId());
		assertEquals("AppointmentMemberDTO.Name", dtoMember.getName(), member.getName());
		assertEquals("AppointmentMemberDTO.AppointmentRequestId", dtoMember.getAppointmentRequestId(),
				member.getAppointmentRequest().getId());
		assertEquals("AppointmentMemberDTO.AppointmentDecisions.size", dtoMember.getAppointmentDecisions().size(),
				member.getAppointmentDecisions().size());
	}
}
