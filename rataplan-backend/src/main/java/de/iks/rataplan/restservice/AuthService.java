package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.restservice.NotificationType;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface AuthService {
    String CLAIM_USERID = "user_id";
    
    AuthUser getUserData(Jwt token);
    String fetchDisplayName(Integer userId);
    Integer fetchUserIdFromEmail(String email);
    default void sendNotification(
        int recipient, NotificationType type, String subject, String content, String summaryContent
    )
    {
        sendUserNotifications(List.of(recipient), type, subject, content, summaryContent);
    }
    default void sendNotification(int recipient, NotificationType type, String subject, String content) {
        sendNotification(recipient, type, subject, content, content);
    }
    default void sendNotifications(
        int[] recipients, NotificationType type, String subject, String content, String summaryContent
    )
    {
        sendUserNotifications(
            Arrays.stream(recipients).boxed().collect(Collectors.toUnmodifiableList()),
            type,
            subject,
            content,
            summaryContent
        );
    }
    default void sendNotifications(int[] recipients, NotificationType type, String subject, String content) {
        sendNotifications(recipients, type, subject, content, content);
    }
    void sendUserNotifications(
        Collection<Integer> recipients, NotificationType type, String subject, String content, String summaryContent
    );
    default void sendUserNotifications(
        Collection<Integer> recipients, NotificationType type, String subject, String content
    )
    {
        sendUserNotifications(recipients, type, subject, content, content);
    }
    default void sendNotification(
        String email, NotificationType type, String subject, String content, String summaryContent
    )
    {
        sendMailNotifications(List.of(email), type, subject, content, summaryContent);
    }
    default void sendNotification(String email, NotificationType type, String subject, String content) {
        sendNotification(email, type, subject, content, content);
    }
    default void sendNotifications(
        String[] emails, NotificationType type, String subject, String content, String summaryContent
    )
    {
        sendMailNotifications(Arrays.asList(emails), type, subject, content, summaryContent);
    }
    default void sendNotifications(String[] emails, NotificationType type, String subject, String content) {
        sendNotifications(emails, type, subject, content, content);
    }
    void sendMailNotifications(
        Collection<String> emails, NotificationType type, String subject, String content, String summaryContent
    );
    default void sendMailNotifications(
        Collection<String> emails, NotificationType type, String subject, String content
    )
    {
        sendMailNotifications(emails, type, subject, content, content);
    }
}