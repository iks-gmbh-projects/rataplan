package de.iks.rataplan.service;

import de.iks.rataplan.domain.ConfirmAccountMailData;
import de.iks.rataplan.domain.FeedbackCategory;
import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.ResetPasswordMailData;
import de.iks.rataplan.domain.notifications.NotificationMailData;
import de.iks.rataplan.dto.FeedbackDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MailService {
    void sendMailForResetPassword(ResetPasswordMailData resetPasswordMailData);
    void sendAccountConfirmationEmail(ConfirmAccountMailData confirmAccountMailData);
    void sendNotification(String recipient, NotificationMailData notification);
    void sendNotificationSummary(String recipient, Collection<? extends NotificationMailData> notifications);
    void notifyParticipantDeletion(ParticipantDeletionMailData participantDeletionMailData);
    void sendFeedbackReport(Map<FeedbackCategory, ? extends List<? extends FeedbackDTO>> feedback);
}
