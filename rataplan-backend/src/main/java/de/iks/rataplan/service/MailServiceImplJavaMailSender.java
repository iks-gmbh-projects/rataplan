package de.iks.rataplan.service;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.utils.MailBuilderJavaMailSender;

@Service
public class MailServiceImplJavaMailSender implements MailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendgridApiKey;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailBuilderJavaMailSender mailBuilder;

    @Override
    public void sendMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest) {
        MimeMessage message = (MimeMessage) mailBuilder.buildMailForAppointmentRequestCreation(appointmentRequest);
        mailSender.send(message);
    }

    @Override
    public void sendMailForAppointmentRequestInvitations(AppointmentRequest appointmentRequest) {
        // TODO not implemented yet

    }

    @Override
    public void sendMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest) {
        // TODO not implemented yet

    }

    @Override
    public void sendMailForContactRequest(ContactData contactData) {
        // TODO not implemented yet
    }

}
