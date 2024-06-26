package de.iks.rataplan.utils;

import de.iks.rataplan.domain.ContactData;
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
    
    private final SendSmtpEmailSender sender;
    
    @Value("${mail.contactTo}")
    private String contactMailTo;
    
    private final TemplateEngine templateEngine;
    
    public SendSmtpEmail buildMailForContactRequest(ContactData contactData) {
        Context ctx = new Context();
        ctx.setVariable("subject", contactData.getSubject());
        ctx.setVariable("senderMail", contactData.getSenderMail());
        ctx.setVariable("content", contactData.getContent());
        
        String subjectContent = templateEngine.process("contact_subject", ctx);
        
        String htmlContent = templateEngine.process("contact_htmlContent", ctx);
        
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(this.contactMailTo) //???
            ))
            .subject(subjectContent)
            .htmlContent(htmlContent);
    }
}
