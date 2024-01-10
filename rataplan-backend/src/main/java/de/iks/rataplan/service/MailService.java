package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.ContactData;

public interface MailService {
    void sendMailForVoteCreation(Vote vote);

    void sendMailForVoteInvitations(Vote vote);

    void sendMailForVoteExpired(Vote vote);

    void sendMailForContactRequest(ContactData contactData);

}
