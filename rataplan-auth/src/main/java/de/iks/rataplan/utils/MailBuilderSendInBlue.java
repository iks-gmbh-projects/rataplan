package de.iks.rataplan.utils;

import de.iks.rataplan.config.EmailConfig;
import de.iks.rataplan.config.FrontendConfig;
import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.FeedbackDTO;
import lombok.RequiredArgsConstructor;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MailBuilderSendInBlue {
    private final FrontendConfig frontendConfig;
    
    private final TemplateEngine templateEngine;
    private final SendSmtpEmailSender sender;
    private final EmailConfig emailConfig;
    
    public SendSmtpEmail buildMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        String resetPasswordLink =
            frontendConfig.getUrl() + "/reset-password?token=" + resetPasswordMailData.getToken();
        
        Context ctx = new Context();
        ctx.setVariable("link", resetPasswordLink);
        
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(resetPasswordMailData.getMail())))
            .subject(templateEngine.process("resetPassword_subject", ctx))
            .htmlContent(templateEngine.process("resetPassword_content", ctx));
    }
    
    public SendSmtpEmail buildAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        String confirmAccountLink = frontendConfig.getUrl() + "/confirm-account/" + confirmAccountMailData.getToken();
        
        Context ctx = new Context();
        ctx.setVariable("link", confirmAccountLink);
        
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(confirmAccountMailData.getEmailAddress())))
            .subject(templateEngine.process("confirmAccount_subject", ctx))
            .htmlContent(templateEngine.process("confirmAccount_content", ctx));
    }
    
    public SendSmtpEmail buildFeedbackReportMail(
        Map<FeedbackCategory, ? extends List<? extends FeedbackDTO>> feedback
    )
    {
        Context ctx = new Context();
        ctx.setVariable("feedbackMap", feedback);
        
        return new SendSmtpEmail().sender(sender)
            .to(emailConfig.getFeedback()
                .stream()
                .map(e -> new SendSmtpEmailTo().email(e))
                .collect(Collectors.toUnmodifiableList()))
            .subject("Feedback Report")
            .htmlContent(templateEngine.process("feedbackReport_content", ctx));
    }
    public SendSmtpEmail buildNotificationMail(String recipient, NotificationMailData notification) {
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(recipient)))
            .subject(notification.getSubject())
            .htmlContent(notification.getContent());
    }
    public SendSmtpEmail buildNotificationSummaryMail(
        String recipient,
        Collection<? extends NotificationMailData> notifications
    )
    {
        Context ctx = new Context();
        ctx.setVariable("notifications", notifications);
        return new SendSmtpEmail().sender(sender)
            .to(Collections.singletonList(new SendSmtpEmailTo().email(recipient)))
            .subject(templateEngine.process("notification_summary_subject", ctx))
            .htmlContent(templateEngine.process("notification_summary_content", ctx));
    }
}