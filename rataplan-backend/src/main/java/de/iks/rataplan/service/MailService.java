package de.iks.rataplan.service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.domain.ResetPasswordMailData;

public interface MailService {
    void sendMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest);

    void sendMailForAppointmentRequestInvitations(AppointmentRequest appointmentRequest);

    void sendMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest);

    void sendMailForContactRequest(ContactData contactData);

    void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData);

}
