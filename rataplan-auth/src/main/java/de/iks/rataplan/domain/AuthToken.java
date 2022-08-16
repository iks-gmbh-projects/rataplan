package de.iks.rataplan.domain;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "auth_token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthToken {

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
