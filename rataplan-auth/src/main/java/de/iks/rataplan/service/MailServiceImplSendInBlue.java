package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
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

@Primary
@Service
@RequiredArgsConstructor
@ConditionalOnBean(TransactionalEmailsApi.class)
@Slf4j
public class MailServiceImplSendInBlue implements MailService {
    
    private final MailBuilderSendInBlue mailBuilder;
    private final TransactionalEmailsApi transactionalEmailsApi;
    
    private final Environment environment;
    
    @Override
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        SendSmtpEmail mail = mailBuilder.buildMailForResetPassword(resetPasswordMailData);
        try {
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        SendSmtpEmail mail = mailBuilder.buildAccountConfirmationEmail(confirmAccountMailData);
        try {
            log.info(mail.getHtmlContent());
            transactionalEmailsApi.sendTransacEmail(mail);
        } catch (ApiException ex) {
            log.error("API-Exception: {}\n{}\n{}", ex.getCode(), ex.getResponseHeaders(), ex.getResponseBody());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void notifyParticipantDeletion(ParticipantDeletionMailData participantDeletionMailData) throws ApiException {
        SendSmtpEmail mail = mailBuilder.buildParticipantDeletionEmail(participantDeletionMailData);
        transactionalEmailsApi.sendTransacEmail(mail);
    }

    private boolean isInProdMode() {
        return "true".equals(environment.getProperty("RATAPLAN.PROD"));
    }
}
