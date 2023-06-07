package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.ContactData;

public interface MailService {
    void sendMailForAppointmentRequestCreation(Vote vote);

    void sendMailForAppointmentRequestInvitations(Vote vote);

    void sendMailForAppointmentRequestExpired(Vote vote);

    void sendMailForContactRequest(ContactData contactData);

}
