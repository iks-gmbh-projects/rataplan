package de.iks.rataplan.domain.notifications;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "notification_type")
public class NotificationType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @ManyToOne(cascade = {
        CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "categoryId")
    private NotificationCategory category;
    
    @Column(name = "name", unique = true, updatable = false)
    private String name;
    
    private boolean suppressAnonymous;
}