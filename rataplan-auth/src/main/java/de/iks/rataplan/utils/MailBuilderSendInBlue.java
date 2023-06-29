package de.iks.rataplan.utils;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;

import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

@Service
@RequiredArgsConstructor
public class MailBuilderSendInBlue {
    @Value("${rataplan.frontend.url}")
    private String baseUrl;

    private final TemplateEngine templateEngine;
    private final SendSmtpEmailSender sender;

    public SendSmtpEmail buildMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        String resetPasswordLink = baseUrl + "/reset-password?token=" + resetPasswordMailData.getToken();

        Context ctx = new Context();
        ctx.setVariable("link", resetPasswordLink);

        return new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(
                        new SendSmtpEmailTo()
                                .email(resetPasswordMailData.getMail())
                ))
                .subject(templateEngine.process("resetPassword_subject", ctx))
                .htmlContent(templateEngine.process("resetPassword_content", ctx))
                ;
    }

    public SendSmtpEmail buildAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        return new SendSmtpEmail()
                .sender(sender)
                .subject("Konto Bestätigung")
                .to(Collections
                        .singletonList(new SendSmtpEmailTo()
                                .email(confirmAccountMailData.getEmailAddress()))).htmlContent(" < html >\n" +
                        " <head > \n" +
                        "<title > Email Template </title > \n" +
                        "</head > \n" +
                        "<body > \n" +
                        "<h2 > Klicken Sie den Knopf, um Ihr Konto zu aktivieren</h2 >\n " +
                        "<p > \n" +
                        "<a href = drumdibum.com/confirm-account/" + confirmAccountMailData.getToken() + "> Konto Bestätigen </a >\n" +
                        " </p > \n" +
                        "</body > \n" +
                        "</html >");
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
