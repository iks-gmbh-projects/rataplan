package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.dto.restservice.NotificationType;

public interface AuthService {
    AuthUser getUserData(String token) throws InvalidTokenException;
    String fetchDisplayName(Integer userId);
    Integer fetchUserIdFromEmail(String email);
    boolean isValidIDToken(String token);
    void sendNotification(int recipient, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotification(int recipient, NotificationType type, String subject, String content) {
        sendNotification(recipient, type, subject, content, content);
    }
    void sendNotification(String email, NotificationType type, String subject, String content, String summaryContent);
    default void sendNotification(String email, NotificationType type, String subject, String content) {
        sendNotification(email, type, subject, content, content);
    }
}
