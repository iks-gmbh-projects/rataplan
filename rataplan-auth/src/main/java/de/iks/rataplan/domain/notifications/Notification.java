package de.iks.rataplan.domain.notifications;

import de.iks.rataplan.domain.User;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_queue")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Transient
    private String recipientMail;
    @ManyToOne
    @JoinColumn(name = "recipientId")
    private User recipient;
    
    @ManyToOne
    @JoinColumn(name = "typeId")
    private NotificationType type;
    
    @CreationTimestamp
    @Column(name = "creationTime", updatable = false)
    private Timestamp creationTime;
    
    private byte[] subject;
    private byte[] content;
    @Transient
    private String fullContent;
    
    public Notification(
        String recipientMail, User recipient, NotificationType type, byte[] subject, byte[] content, String fullContent
    )
    {
        this(null, recipientMail, recipient, type, null, subject, content, fullContent);
    }
    
    public Notification(
        String recipient, NotificationType type, byte[] subject, byte[] content, String fullContent
    )
    {
        this(null, recipient, null, type, null, subject, content, fullContent);
    }
    
    public EmailCycle getCycle() {
        if(recipient == null) return type.isSuppressAnonymous() ? EmailCycle.SUPPRESS : EmailCycle.INSTANT;
        return Optional.ofNullable(recipient.getNotificationTypeSettings().get(type))
            .or(() -> Optional.ofNullable(recipient.getNotificationCategorySettings().get(type.getCategory())))
            .orElse(recipient.getDefaultEmailCycle());
    }
}
