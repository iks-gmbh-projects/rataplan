package de.iks.rataplan.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.iks.rataplan.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "rataplanuser")
@ToString
@Getter
@Setter
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1171464424149123656L;

    private Timestamp creationTime;
    private Timestamp lastUpdated;
    private Integer version;

    private Integer id;
    private String mail;
    private String username;
    private String password;
    private String displayname;
    @JsonIgnore
    private boolean encrypted;

    private boolean accountConfirmed;

    public User(Integer id, String mail, String username, String password, String displayname, boolean encrypted) {
        this.id = id;
        this.mail = mail;
        this.username = username;
        this.password = password;
        this.displayname = displayname;
        this.encrypted = encrypted;
    }

    public User(Integer id, String mail, String username, String password, String displayname) {
        this(id, mail, username, password, displayname, false);
    }

    public User(UserDTO userDTO) {
        this(userDTO.getId(),
                userDTO.getMail(),
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getDisplayname());
    }
//
//    public User(User cpy) {
//        this(cpy.getId(), cpy.getMail(), cpy.getUsername(), cpy.getPassword(), cpy.getDisplayname(), cpy.isEncrypted());
//    }

    public User() {
        // Required for Hibernate
    }

    @CreationTimestamp
    @Column(updatable = false)
    public Timestamp getCreationTime() {
        return creationTime;
    }

    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    @Version
    public Integer getVersion() {
        return version;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    @Column(name = "mail", unique = true)
    public String getMail() {
        return mail;
    }

    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "displayname")
    public String getDisplayname() {
        return displayname;
    }

}
