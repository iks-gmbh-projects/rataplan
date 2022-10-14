package de.iks.rataplan.service;

import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.utils.MailBuilderSendGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Primary
@Service
public class MailServiceImplSendGrid implements MailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    @Autowired
    private MailBuilderSendGrid mailBuilder;

    @Autowired
    private Environment environment;

    @Override
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {

        Mail mail = mailBuilder.buildMailForResetPassword(resetPasswordMailData);


        SendGrid sendGrid = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            if (isInProdMode()) {
                sendGrid.api(request);
            }
        } catch (IOException ex) {
            throw new MailPreparationException(ex);
        }

    }

    private boolean isInProdMode() {
        return "true".equals(environment.getProperty("RATAPLAN.PROD"));
    }
}
