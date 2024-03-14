package de.iks.rataplan.utils;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.FeedbackDTO;
import lombok.RequiredArgsConstructor;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import org.springframework.beans.factory.annotation.Value;
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
    @Value("${rataplan.frontend.url}")
    private String baseUrl;

    private final TemplateEngine templateEngine;
    private final SendSmtpEmailSender sender;
    @Value("${mail.feedback:}")
    private List<String> feedbackReceivers;

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
                .htmlContent(templateEngine.process("resetPassword_content", ctx));
    }

    public SendSmtpEmail buildAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        String confirmAccountLink = baseUrl + "/confirm-account/" + confirmAccountMailData.getToken();

        Context ctx = new Context();
        ctx.setVariable("link", confirmAccountLink);

        return new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(
                        new SendSmtpEmailTo()
                                .email(confirmAccountMailData.getEmailAddress())
                ))
                .subject(templateEngine.process("confirmAccount_subject",ctx))
                .htmlContent(templateEngine.process("confirmAccount_content",ctx));
    }

    public SendSmtpEmail buildParticipantDeletionEmail(ParticipantDeletionMailData participantDeletionMailData){
        String voteLink = baseUrl + "/vote/" + participantDeletionMailData.getVoteToken();

        Context ctx = new Context();
        ctx.setVariable("link", voteLink);

        return new SendSmtpEmail()
                .sender(sender)
                .to(Collections.singletonList(
                        new SendSmtpEmailTo()
                                .email(participantDeletionMailData.getEmail())
                ))
                .subject(templateEngine.process("participantDeletion_subject",ctx))
                .htmlContent(templateEngine.process("participantDeletion_content",ctx));
    }
    public SendSmtpEmail buildFeedbackReportMail(
        Map<FeedbackCategory,? extends List<? extends FeedbackDTO>> feedback
    ) {
        Context ctx = new Context();
        ctx.setVariable("feedbackMap", feedback);
        
        return new SendSmtpEmail()
            .sender(sender)
            .to(
                feedbackReceivers.stream()
                    .map(e -> new SendSmtpEmailTo().email(e))
                    .collect(Collectors.toUnmodifiableList())
            )
            .subject("Feedback Report")
            .htmlContent(templateEngine.process("feedbackReport_content", ctx));
    }
    public SendSmtpEmail buildNotificationMail(String recipient, NotificationMailData notification) {
        return new SendSmtpEmail()
            .sender(sender)
            .to(Collections.singletonList(
                new SendSmtpEmailTo()
                    .email(recipient)
            ))
            .subject(notification.getSubject())
            .htmlContent(notification.getContent());
    }
    public SendSmtpEmail buildNotificationSummaryMail(String recipient, Collection<? extends NotificationMailData> notifications) {
        Context ctx = new Context();
        ctx.setVariable("notifications", notifications);
        return new SendSmtpEmail()
            .sender(sender)
            .to(Collections.singletonList(
                new SendSmtpEmailTo()
                    .email(recipient)
            ))
            .subject(templateEngine.process("notification_summary_subject", ctx))
            .htmlContent(templateEngine.process("notification_summary_content", ctx));
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
