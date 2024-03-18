package de.iks.rataplan.service;

import de.iks.rataplan.domain.ContactData;

public interface MailService {
    void sendMailForContactRequest(ContactData contactData);
}
