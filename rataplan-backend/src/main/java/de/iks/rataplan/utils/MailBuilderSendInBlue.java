package de.iks.rataplan.utils;

import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.domain.Vote;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MailBuilderSendInBlue {
    
    /*
     * Sendgrid mailbuilder Github:
     * https://github.com/sendgrid/sendgrid-java/blob/master/examples/helpers/
     * mail/Example.java#L42
     *
     */
    private final SendSmtpEmailSender sender;
    
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    
    @Value("${mail.contactTo}")
    private String contactMailTo;
    
    private final TemplateEngine templateEngine;
    
    private final CryptoService cryptoService;
    
    private final FromEncryptedStringConverter fromEncryptedStringConverter;
    
    public SendSmtpEmail buildMailForVoteExpired(Vote vote) {
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        String url = baseUrl + "/vote/" + participationToken;
        
        Context ctx = new Context();
        ctx.setVariable("url", url);
        ctx.setVariable("title", fromEncryptedStringConverter.convert(vote.getTitle()));
        
        String subjectContent = templateEngine.process("expired_subject", ctx);
        
        //		String plainContent = createPlainContent(url, adminUrl);
        //		content.setType("text/plain");
        //		content.setValue(plainContent);
        //		mail.addContent(content);
        
        String htmlContent = templateEngine.process("expired_content", ctx);
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(cryptoService.decryptDBRaw(vote.getNotificationSettings()
                .getRecipientEmail()))))
            .subject(subjectContent)
            .htmlContent(htmlContent);
    }
    
    public SendSmtpEmail buildMailForVoteCreation(Vote vote) {
        String participationToken = vote.getParticipationToken();
        if(participationToken == null) participationToken = vote.getId().toString();
        String url = baseUrl + "/vote/" + participationToken;
        String editToken = vote.getEditToken();
        String adminUrl = editToken == null ? null : baseUrl + "/vote/" + editToken + "/edit";
        
        Context ctx = new Context();
        ctx.setVariable("url", url);
        ctx.setVariable("adminUrl", adminUrl);
        
        String subjectContent = templateEngine.process("to_organizerMail_subject", ctx);
        
        //		String plainContent = createPlainContent(url, adminUrl);
        //		content.setType("text/plain");
        //		content.setValue(plainContent);
        //		mail.addContent(content);
        
        String htmlContent = templateEngine.process("to_organizerMail_htmlContent", ctx);
        
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(cryptoService.decryptDBRaw(vote.getNotificationSettings()
                .getRecipientEmail()))))
            .subject(subjectContent)
            .htmlContent(htmlContent);
    }
    
    public SendSmtpEmail buildMailForContactRequest(ContactData contactData) {
        //        Email toMail = new Email();
        //        toMail.setEmail(this.contactMailTo); //???? warum?
        //        personalization.addTo(toMail);
        
        Context ctx = new Context();
        ctx.setVariable("subject", contactData.getSubject());
        ctx.setVariable("senderMail", contactData.getSenderMail());
        ctx.setVariable("content", contactData.getContent());
        
        String subjectContent = templateEngine.process("contact_subject", ctx);
        
        //		String plainContent = createPlainContent(url, adminUrl);
        //		content.setType("text/plain");
        //		content.setValue(plainContent);
        //		mail.addContent(content);
        
        String htmlContent = templateEngine.process("contact_htmlContent", ctx);
        
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(this.contactMailTo) //???
            ))
            .subject(subjectContent)
            .htmlContent(htmlContent);
    }
    
    // f�r plain/text ist "\r\n" in Java ein Zeilenumbruch
    //	private String createPlainContent(String url, String adminUrl) {
    //		return "Hallo! \r\n\r\n\r\n"
    //				+ "Sie haben soeben eine neue Terminanfrage erstellt. Jetzt m�ssen nur noch alle abstimmen:
    //				\r\n\r\n"
    //				+ url + " \r\n\r\n"
    //				+ "Falls Sie die Terminanfrage bearbeiten m�chten, k�nnen Sie dies unter folgendem Link tun:
    //				\r\n\r\n"
    //				+ adminUrl + "\r\n\r\n\r\n" + "Vielen Dank, dass Sie rataplan benutzen.\r\n\r\n"
    //				+ "Hinweis: HTML-Inhalte werden nicht dargestellt.";
    //	}
}
