package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.dto.restservice.NotificationType;

import java.util.Collection;

public interface AuthService {
    AuthUser getUserData(String token) throws InvalidTokenException;
    String fetchDisplayName(Integer userId);
    Integer fetchUserIdFromEmail(String email);
    boolean isValidIDToken(String token);
    void sendNotification(int recipient, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotification(int recipient, NotificationType type, String subject, String content) {
        sendNotification(recipient, type, subject, content, content);
    }
    void sendNotifications(int[] recipients, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotifications(int[] recipients, NotificationType type, String subject, String content) {
        sendNotifications(recipients, type, subject, content, content);
    }
    void sendUserNotifications(Collection<Integer> recipients, NotificationType type, String subject, String content, String summaryContent);
    default void sendUserNotifications(Collection<Integer> recipients, NotificationType type, String subject, String content) {
        sendUserNotifications(recipients, type, subject, content, content);
    }
    void sendNotification(String email, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotification(String email, NotificationType type, String subject, String content) {
        sendNotification(email, type, subject, content, content);
    }
    void sendNotifications(String[] emails, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotifications(String[] emails, NotificationType type, String subject, String content) {
        sendNotifications(emails, type, subject, content, content);
    }
    void sendMailNotifications(Collection<String> emails, NotificationType type, String subject, String content, String summaryContent);
    default void sendMailNotifications(Collection<String> emails, NotificationType type, String subject, String content) {
        sendMailNotifications(emails, type, subject, content, content);
    }
}
