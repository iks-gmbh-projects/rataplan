package de.iks.rataplan.service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;

public interface MailService {
	public void sendMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest);
	public void sendMailForAppointmentRequestInvitations(AppointmentRequest appointmentRequest);
	public void sendMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest);
	public void sendMailForContactRequest(ContactData contactData);
	
}
