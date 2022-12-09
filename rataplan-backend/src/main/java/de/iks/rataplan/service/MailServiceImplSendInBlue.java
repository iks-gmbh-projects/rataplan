package de.iks.rataplan.service;

import java.util.List;

import de.iks.rataplan.utils.MailBuilderSendInBlue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;

@Primary
@Service
@ConditionalOnBean(TransactionalEmailsApi.class)
public class MailServiceImplSendInBlue implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImplSendInBlue.class);
    private final MailBuilderSendInBlue mailBuilder;
    private final TransactionalEmailsApi transactionalEmailsApi;

    private final Environment environment;
    
    public MailServiceImplSendInBlue(
        MailBuilderSendInBlue mailBuilder,
        TransactionalEmailsApi transactionalEmailsApi,
        Environment environment
    ) {
        this.mailBuilder = mailBuilder;
        this.transactionalEmailsApi = transactionalEmailsApi;
        this.environment = environment;
    }
    
    @Override
    public void sendMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest) {
        SendSmtpEmail mail = mailBuilder.buildMailForAppointmentRequestCreation(appointmentRequest);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new MailPreparationException(ex);
        }
    }

    @Override
    public void sendMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest) {
        SendSmtpEmail mail = mailBuilder.buildMailForAppointmentRequestExpired(appointmentRequest);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new MailPreparationException(ex);
        }
    }

    @Override
    public void sendMailForAppointmentRequestInvitations(AppointmentRequest appointmentRequest) {
        List<SendSmtpEmail> mailList = mailBuilder.buildMailListForAppointmentRequestInvitations(appointmentRequest);

        for (SendSmtpEmail mail : mailList) {
            try {
                transactionalEmailsApi.sendTransacEmail(mail);
            } catch (ApiException ex) {
                log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
                throw new MailPreparationException(ex);
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
            throw new MailPreparationException(ex);
        }
    }

    private boolean isInProdMode() {
        return "true".equals(environment.getProperty("RATAPLAN.PROD"));
    }
}
