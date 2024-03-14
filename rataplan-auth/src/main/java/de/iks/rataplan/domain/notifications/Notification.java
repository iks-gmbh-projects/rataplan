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
    @JoinColumn(name = "categoryId")
    private NotificationCategory category;
    
    @CreationTimestamp
    @Column(name = "creationTime", updatable = false)
    private Timestamp creationTime;
    
    private byte[] subject;
    private byte[] content;
    @Transient
    private String fullContent;
    
    public Notification(
        String recipientMail,
        User recipient,
        NotificationCategory category,
        byte[] subject,
        byte[] content,
        String fullContent
    )
    {
        this(null, recipientMail, recipient, category, null, subject, content, fullContent);
    }
    
    public Notification(
        String recipient, NotificationCategory category, byte[] subject, byte[] content, String fullContent
    )
    {
        this(null, recipient, null, category, null, subject, content, fullContent);
    }
    
    public EmailCycle getCycle() {
        if(recipient == null) return category.isSuppressAnonymous() ? EmailCycle.SUPPRESS : EmailCycle.INSTANT;
        return Optional.ofNullable(recipient.getNotificationSettings().get(category))
            .orElse(recipient.getDefaultEmailCycle());
    }
}
