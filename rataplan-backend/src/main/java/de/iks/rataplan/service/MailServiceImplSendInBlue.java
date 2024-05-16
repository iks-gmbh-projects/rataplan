package de.iks.rataplan.service;

import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;

@RequiredArgsConstructor
@Slf4j
public class MailServiceImplSendInBlue implements MailService {
    private final MailBuilderSendInBlue mailBuilder;
    private final TransactionalEmailsApi transactionalEmailsApi;

    @Override
    public void sendMailForContactRequest(ContactData contactData) {
        SendSmtpEmail mail = mailBuilder.buildMailForContactRequest(contactData);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }
}