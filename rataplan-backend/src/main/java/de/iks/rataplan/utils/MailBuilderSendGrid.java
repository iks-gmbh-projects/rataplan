package de.iks.rataplan.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Personalization;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.ContactData;

@Service
public class MailBuilderSendGrid {

	/*
	 * Sendgrid mailbuilder Github:
	 * https://github.com/sendgrid/sendgrid-java/blob/master/examples/helpers/
	 * mail/Example.java#L42
	 * 
	 */
	@Value("${mail.from}")
	private String from;

	@Value("${rataplan.frontend.url}")
	private String baseUrl;

	@Value("${mail.contactTo}")
	private String contactMailTo;

	@Autowired
	private TemplateEngine templateEngine;

	public List<Mail> buildMailListForAppointmentRequestInvitations(AppointmentRequest appointmentRequest) {
		String url = baseUrl + "/appointmentrequest/" + appointmentRequest.getId();

		List<Mail> mailList = new ArrayList<>();

		Email from = new Email();
		from.setName("drumdibum");
		from.setEmail("donotreply@drumdibum.de");

		for (String consignee : appointmentRequest.getConsigneeList()) {
			Mail mail = new Mail();

			Email to = new Email();
			to.setName(consignee);
			to.setEmail(consignee);

			Personalization personalization = new Personalization();
			personalization.addTo(to);

			mail.addPersonalization(personalization);
			mail.setFrom(from);

			Context ctx = new Context();
			ctx.setVariable("url", url);

			String subjectString = templateEngine.process("invitation_subject", ctx);
			String contentString = templateEngine.process("invitation_content", ctx);

			Content content = new Content();
			content.setType("text/html");
			content.setValue(contentString);

			mail.setSubject(subjectString);
			mail.addContent(content);

			mailList.add(mail);
		}

		return mailList;
	}
	
	public Mail buildMailForAppointmentRequestExpired(AppointmentRequest appointmentRequest) {
		String url = baseUrl + "/appointmentrequest/" + appointmentRequest.getId();

		Mail mail = new Mail();

		Email fromEmail = new Email();
		fromEmail.setName("drumdibum");
		fromEmail.setEmail("donotreply@drumdibum.de");
		mail.setFrom(fromEmail);

		Personalization personalization = new Personalization();

		Email toMail = new Email();
		toMail.setEmail(appointmentRequest.getOrganizerMail());
		personalization.addTo(toMail);

		mail.addPersonalization(personalization);

		Context ctx = new Context();
		ctx.setVariable("url", url);
		ctx.setVariable("title", appointmentRequest.getTitle());

		String subjectContent = templateEngine.process("expired_subject", ctx);
		mail.setSubject(subjectContent);

		Content content = new Content();

//		String plainContent = createPlainContent(url, adminUrl);
//		content.setType("text/plain");
//		content.setValue(plainContent);
//		mail.addContent(content);

		String htmlContent = templateEngine.process("expired_content", ctx);
		content.setType("text/html");
		content.setValue(htmlContent);
		mail.addContent(content);

		return mail;
	}

	public Mail buildMailForAppointmentRequestCreation(AppointmentRequest appointmentRequest) {
		String url = baseUrl + "/appointmentrequest/" + appointmentRequest.getId();
		String adminUrl = baseUrl + "/appointmentrequest/" + appointmentRequest.getId() + "/edit";

		Mail mail = new Mail();

		Email fromEmail = new Email();
		fromEmail.setName("drumdibum");
		fromEmail.setEmail("donotreply@drumdibum.de");
		mail.setFrom(fromEmail);

		Personalization personalization = new Personalization();

		Email toMail = new Email();
		toMail.setEmail(appointmentRequest.getOrganizerMail());
		personalization.addTo(toMail);

		mail.addPersonalization(personalization);

		Context ctx = new Context();
		ctx.setVariable("url", url);
		ctx.setVariable("adminUrl", adminUrl);

		String subjectContent = templateEngine.process("to_organizerMail_subject", ctx);
		mail.setSubject(subjectContent);

		Content content = new Content();

//		String plainContent = createPlainContent(url, adminUrl);
//		content.setType("text/plain");
//		content.setValue(plainContent);
//		mail.addContent(content);

		String htmlContent = templateEngine.process("to_organizerMail_htmlContent", ctx);
		content.setType("text/html");
		content.setValue(htmlContent);
		mail.addContent(content);

		return mail;
	}

	public Mail buildMailForContactRequest(ContactData contactData) {
		Mail mail = new Mail();

		Email fromEmail = new Email();
		fromEmail.setName("drumdibum");
		fromEmail.setEmail("donotreply@drumdibum.de");
		mail.setFrom(fromEmail);

		Personalization personalization = new Personalization();

		Email toMail = new Email();
		toMail.setEmail(this.contactMailTo);
		personalization.addTo(toMail);
		
		mail.addPersonalization(personalization);
		
		Context ctx = new Context();
		ctx.setVariable("subject", contactData.getSubject());
		ctx.setVariable("senderMail", contactData.getSenderMail());
		ctx.setVariable("content", contactData.getContent());

		String subjectContent = templateEngine.process("contact_subject", ctx);
		mail.setSubject(subjectContent);

		Content content = new Content();

//		String plainContent = createPlainContent(url, adminUrl);
//		content.setType("text/plain");
//		content.setValue(plainContent);
//		mail.addContent(content);

		String htmlContent = templateEngine.process("contact_htmlContent", ctx);
		content.setType("text/html");
		content.setValue(htmlContent);
		mail.addContent(content);
				
		return mail;
	}
	
	
	// f�r plain/text ist "\r\n" in Java ein Zeilenumbruch
//	private String createPlainContent(String url, String adminUrl) {
//		return "Hallo! \r\n\r\n\r\n"
//				+ "Sie haben soeben eine neue Terminanfrage erstellt. Jetzt m�ssen nur noch alle abstimmen: \r\n\r\n"
//				+ url + " \r\n\r\n"
//				+ "Falls Sie die Terminanfrage bearbeiten m�chten, k�nnen Sie dies unter folgendem Link tun: \r\n\r\n"
//				+ adminUrl + "\r\n\r\n\r\n" + "Vielen Dank, dass Sie rataplan benutzen.\r\n\r\n"
//				+ "Hinweis: HTML-Inhalte werden nicht dargestellt.";
//	}
}
