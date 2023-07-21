package de.iks.rataplan.service;

import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;

import java.util.List;

@Primary
@Service
@ConditionalOnBean(TransactionalEmailsApi.class)
@RequiredArgsConstructor
@Slf4j
public class MailServiceImplSendInBlue implements MailService {
    private final MailBuilderSendInBlue mailBuilder;
    private final TransactionalEmailsApi transactionalEmailsApi;

    private final Environment environment;
    
    @Override
    public void sendMailForVoteCreation(Vote vote) {
        SendSmtpEmail mail = mailBuilder.buildMailForVoteCreation(vote);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendMailForVoteExpired(Vote vote) {
        SendSmtpEmail mail = mailBuilder.buildMailForVoteExpired(vote);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendMailForVoteInvitations(Vote vote) {
        List<SendSmtpEmail> mailList = mailBuilder.buildMailListForVoteInvitations(vote);

        for (SendSmtpEmail mail : mailList) {
            try {
                transactionalEmailsApi.sendTransacEmail(mail);
            } catch (ApiException ex) {
                log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
                throw new RuntimeException(ex);
            }
        }
    }

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
