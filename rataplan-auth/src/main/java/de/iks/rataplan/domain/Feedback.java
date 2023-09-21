package de.iks.rataplan.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
public class Feedback {
    @CreationTimestamp
    private Timestamp creationTime;
    @UpdateTimestamp
    private Timestamp lastUpdated;
    @Version
    private Integer version;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private byte[] title;
    private byte[] text;
    private byte rating;
    private FeedbackCategory category;
    private boolean sent = false;
    
    public Feedback(byte[] title, byte[] text, byte rating, FeedbackCategory category) {
        this.title = title;
        this.text = text;
        this.rating = rating;
        this.category = category;
    }
}
