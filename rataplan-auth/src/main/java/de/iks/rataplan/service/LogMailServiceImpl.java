package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.FeedbackDTO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(value = "RATAPLAN.PROD", havingValue = "false", matchIfMissing = true)
@Slf4j
public class LogMailServiceImpl implements MailService {
    @Value("${rataplan.frontend.url}")
    private String baseUrl;
    @Override
    public void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData) {
        log.info("Reset password link: {}", baseUrl + "/reset-password?token=" + resetPasswordMailData.getToken());
    }

    @Override
    public void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData) {
        log.info("Account confirmation link: " + baseUrl + "/confirm-account/" + confirmAccountMailData.getToken());
    }
    
    @Override
    public void sendFeedbackReport(Map<FeedbackCategory, ? extends List<? extends FeedbackDTO>> feedback) {
        feedback.forEach((category, categoryFeedback) -> {
            log.info("{} Feedback:", categoryFeedback);
            for(FeedbackDTO f : categoryFeedback) {
                log.info("    {} {}:\n{}\n", "*".repeat(f.getRating()), f.getTitle(), f.getText());
            }
        });
    }
    
    @Override
    public void sendNotification(String recipient, NotificationMailData notification) {
        log.info("Notification for {}: {}\n{}", recipient, notification.getSubject(), notification.getContent());
    }
    @Override
    public void sendNotificationSummary(String recipient, Collection<? extends NotificationMailData> notifications) {
        log.info("Notifications for {}:", recipient);
        int i = 0;
        for(NotificationMailData notification : notifications) {
            log.info("{}: {}\n{}", ++i, notification.getSubject(), notification.getContent());
        }
    }
}
