package de.iks.rataplan.domain.notifications;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "notification_category")
public class NotificationCategory {
    @CreationTimestamp
    @Column(name = "creationTime", updatable = false)
    private Timestamp creationTime;
    @UpdateTimestamp
    private Timestamp lastUpdated;
    @Version
    private Integer version;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;
    
    @Column(name = "name", unique = true, updatable = false)
    private String name;
    private boolean suppressAnonymous;
}
