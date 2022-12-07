package de.iks.rataplan.service;

import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.utils.MailBuilderSendInBlue;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;
import sendinblue.ApiException;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;

@Primary
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "RATAPLAN.PROD", havingValue = "true")
@ConditionalOnBean(TransactionalEmailsApi.class)
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
            throw new MailPreparationException(ex);
        }
    }
    
    private boolean isInProdMode() {
        return "true".equals(environment.getProperty("RATAPLAN.PROD"));
    }
}
