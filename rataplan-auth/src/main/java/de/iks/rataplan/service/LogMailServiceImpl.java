package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.dto.FeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

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
    public void notifyParticipantDeletion(ParticipantDeletionMailData participantDeletionMailData) {
        log.info(baseUrl + "/vote/" + participantDeletionMailData.getVoteToken());
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
}
