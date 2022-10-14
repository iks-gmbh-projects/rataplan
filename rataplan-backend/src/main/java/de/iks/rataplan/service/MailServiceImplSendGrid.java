package de.iks.rataplan.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;

import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.utils.MailBuilderSendGrid;

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
    public void sendMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest) {

        Mail mail = mailBuilder.buildMailForAppointmentRequestCreation(appointmentRequest);

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

    @Override
    public void sendMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest) {

        Mail mail = mailBuilder.buildMailForAppointmentRequestExpired(appointmentRequest);

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

    @Override
    public void sendMailForAppointmentRequestInvitations(AppointmentRequest appointmentRequest) {

        List<Mail> mailList = mailBuilder.buildMailListForAppointmentRequestInvitations(appointmentRequest);

        for (Mail mail : mailList) {
            SendGrid sendGrid = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");

            try {
                request.setBody(mail.build());
                if (isInProdMode()) {
                    sendGrid.api(request);
                }
            } catch (IOException ex) {
                throw new MailPreparationException(ex);
            }
        }
    }

    @Override
    public void sendMailForContactRequest(ContactData contactData) {

        Mail mail = mailBuilder.buildMailForContactRequest(contactData);

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
