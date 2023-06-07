package de.iks.rataplan.service;

import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.domain.ContactData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "RATAPLAN.PROD", havingValue = "false", matchIfMissing = true)
public class LogMailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(LogMailServiceImpl.class);
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    
    @Override
    public void sendMailForVoteCreation(Vote vote) {
        log.info("Appointment request created link: {}", baseUrl + "/appointmentrequest/" + vote.getId());
        log.info("Appointment request created admin link: {}", baseUrl + "/appointmentrequest/" + vote.getId() + "/edit");
    }
    
    @Override
    public void sendMailForVoteExpired(Vote vote) {
        log.info("Appointment request expired link: {}", baseUrl + "/appointmentrequest/" + vote.getId());
    }
    
    @Override
    public void sendMailForVoteInvitations(Vote vote) {
        log.info("Appointment request invitation link: {}", baseUrl + "/appointmentrequest/" + vote.getId());
    }
    
    @Override
    public void sendMailForContactRequest(ContactData contactData) {
        log.info("ContactRequest mail sent.");
    }
}
