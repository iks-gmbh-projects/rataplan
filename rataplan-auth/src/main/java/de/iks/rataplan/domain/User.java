package de.iks.rataplan.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.iks.rataplan.dto.UserDTO;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "rataplanuser")
@ToString
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
    
    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }
    
    @UpdateTimestamp
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Version
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Column(name = "mail", unique = true)
    public String getMail() {
        return mail;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    @Column(name = "username", unique = true)
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String name) {
        this.username = name;
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
    
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }
    
    public boolean isEncrypted() {
        return encrypted;
    }
    
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}
