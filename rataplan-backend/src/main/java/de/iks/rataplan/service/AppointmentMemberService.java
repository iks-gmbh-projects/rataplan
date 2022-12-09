package de.iks.rataplan.service;

import de.iks.rataplan.domain.AppointmentMember;
import de.iks.rataplan.domain.AppointmentRequest;

public interface AppointmentMemberService {
	public AppointmentMember createAppointmentMember(AppointmentRequest appointmentRequest,
			AppointmentMember appointmentMember);

	public void deleteAppointmentMember(AppointmentRequest appointmentRequest, AppointmentMember appointmentMember);

	public AppointmentMember updateAppointmentMember(AppointmentRequest appointmentRequest,
			AppointmentMember dbAppointmentMember, AppointmentMember newAppointmentMember);
	
	public void anonymizeAppointmentMember(int memberId);
}
