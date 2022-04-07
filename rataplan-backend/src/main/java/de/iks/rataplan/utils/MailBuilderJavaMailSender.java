package de.iks.rataplan.utils;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import de.iks.rataplan.domain.AppointmentRequest;

@Service
public class MailBuilderJavaMailSender {

	@Value("${mail.from}")
	private String from;

	@Value("${rataplan.frontend.url}")
	private String baseUrl;

	@Value("${mail.contactTo}")
	private String contactMailTo;
	
	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private JavaMailSender mailSender;

	public MimeMessage buildMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest) {
		String url = baseUrl + "/appointmentrequest";
		String adminUrl = baseUrl + "/appointmentrequest";
		String to = appointmentRequest.getOrganizerMail();
		Integer appointmentRequestId = appointmentRequest.getId();

		Context ctx = new Context();
		ctx.setVariable("url", url + "/" + appointmentRequestId);
		ctx.setVariable("adminUrl", adminUrl + "/" + appointmentRequestId + "/edit");
		String htmlContent = templateEngine.process("to_organizerMail_htmlContent", ctx);
		String htmlSubject = templateEngine.process("to_organizerMail_subject", ctx);

		try {
			MimeMessageHelper message = buildDefaultMimeMessageHelper();
			message.setTo(to);
			message.setSubject(htmlSubject);
			message.setText(htmlContent, true);
			return message.getMimeMessage();
		} catch (MessagingException e) {
			throw new MailPreparationException(e);
		}

	}

	private MimeMessageHelper buildDefaultMimeMessageHelper() throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
		message.setFrom(from);
		message.setSentDate(new Date());
		return message;
	}
	
}
