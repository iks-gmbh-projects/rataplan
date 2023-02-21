package de.iks.rataplan.domain;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "auth_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthToken {

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp creationTime;
    @UpdateTimestamp
    private Timestamp lastUpdated;
    @Version
    private Integer version;

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_date_time")
    @CreationTimestamp
    private Timestamp createdDateTime;


    public AuthToken(int id, String token) {
        this.id = id;
        this.token = token;
    }
}
