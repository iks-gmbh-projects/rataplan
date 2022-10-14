package de.iks.rataplan.utils;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Personalization;
import de.iks.rataplan.domain.ResetPasswordMailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailBuilderSendGrid {

    /*
     * Sendgrid mailbuilder Github:
     * https://github.com/sendgrid/sendgrid-java/blob/master/examples/helpers/
     * mail/Example.java#L42
     *
     */
    @Value("${rataplan.frontend.url}")
    private String baseUrl;

    @Autowired
    private TemplateEngine templateEngine;

    public Mail buildMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {

        String resetPasswordLink = baseUrl + "/reset-password?token=" + resetPasswordMailData.getToken();

        Mail mail = new Mail();

        Email fromEmail = new Email();
        fromEmail.setName("drumdibum");
        fromEmail.setEmail("donotreply@drumdibum.de");
        mail.setFrom(fromEmail);

        Personalization personalization = new Personalization();

        Email toMail = new Email();
        toMail.setEmail(resetPasswordMailData.getMail());
        personalization.addTo(toMail);

        mail.addPersonalization(personalization);

        Context ctx = new Context();
        ctx.setVariable("link", resetPasswordLink);

        String subjectContent = templateEngine.process("resetPassword_subject", ctx);
        mail.setSubject(subjectContent);

        Content content = new Content();

        String htmlContent = templateEngine.process("resetPassword_content", ctx);
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
