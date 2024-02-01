package de.iks.rataplan.domain;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

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
    @Column(name = "password")
    private String password;
    @Column(name = "displayname")
    private byte[] displayname;

    private boolean accountConfirmed;

    public User(Integer id, byte[] mail, byte[] username, String password, byte[] displayname) {
        this(null, null, null, id, mail, username, password, displayname, false);
    }
}
