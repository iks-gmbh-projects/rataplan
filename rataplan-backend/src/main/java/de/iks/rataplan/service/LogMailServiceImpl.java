package de.iks.rataplan.service;

import de.iks.rataplan.domain.ContactData;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "RATAPLAN.PROD", havingValue = "false", matchIfMissing = true)
@Slf4j
public class LogMailServiceImpl implements MailService {
    @Override
    public void sendMailForContactRequest(ContactData contactData) {
        log.info("ContactRequest mail sent.");
    }
}
