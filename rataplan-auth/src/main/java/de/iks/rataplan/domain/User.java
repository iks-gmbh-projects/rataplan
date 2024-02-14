package de.iks.rataplan.domain;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.domain.notifications.NotificationCategory;
import de.iks.rataplan.domain.notifications.NotificationType;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rataplanuser")
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1171464424149123656L;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp creationTime;
    @UpdateTimestamp
    private Timestamp lastUpdated;
    @Version
    private Integer version;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "mail", unique = true)
    private byte[] mail;
    @Column(name = "username", unique = true)
    private byte[] username;
    private String password;
    private byte[] displayname;

    private boolean accountConfirmed;
    
    @Column(name = "defaultEmailCycle", insertable = false)
    private EmailCycle defaultEmailCycle;
    
    @ElementCollection
    @CollectionTable(name = "notification_category_setting", joinColumns = @JoinColumn(name = "userId"))
    @MapKeyJoinColumn(name = "categoryId")
    @Column(name = "emailCycle")
    private Map<NotificationCategory, EmailCycle> notificationCategorySettings;
    
    @ElementCollection
    @CollectionTable(name = "notification_type_setting", joinColumns = @JoinColumn(name = "userId"))
    @MapKeyJoinColumn(name = "typeId")
    @Column(name = "emailCycle")
    private Map<NotificationType, EmailCycle> notificationTypeSettings;

    public User(Integer id, byte[] mail, byte[] username, String password, byte[] displayname) {
        this(null, null, null, id, mail, username, password, displayname, false, EmailCycle.INSTANT, null, null);
    }
}
