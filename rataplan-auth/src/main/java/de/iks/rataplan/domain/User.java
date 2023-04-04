package de.iks.rataplan.domain;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @CreationTimestamp
    @Column(updatable = false)
    private Instant creationTime;
    @UpdateTimestamp
    private Instant lastUpdated;
    @Version
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
//
//    public User(User cpy) {
//        this(cpy.getId(), cpy.getMail(), cpy.getUsername(), cpy.getPassword(), cpy.getDisplayname(), cpy.isEncrypted());
//    }

    public User() {
        // Required for Hibernate
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

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
    public String getDisplayname(){ return displayname; }

    public void setDisplayname(String displayname) { this.displayname = displayname; }

    public void trimUserCredentials() {
        username = trimAndNull(username);
        mail = trimAndNull(mail);
        displayname = trimAndNull(displayname);
    }
    
    public boolean invalidFull() {
        return trimAndNull(username) == null ||
            trimAndNull(mail) == null ||
            trimAndNull(displayname) == null ||
            password == null;
    }
    
    public boolean invalidLogin() {
        return (trimAndNull(username) == null && trimAndNull(mail) == null) || password == null;
    }

    public static String trimAndNull(String toTrim) {
        if (toTrim != null) {
            toTrim = toTrim.trim();
            if (toTrim.isEmpty()) {
                return null;
            }
        }
        return toTrim;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @PrePersist
    @PreUpdate
    public void hibernateCreateDate() {
        final Instant now = Instant.now();
        if(this.creationTime == null) this.creationTime = now;
        this.lastUpdated = now;
        if(this.version == null) this.version = 1;
        else this.version++;
    }

	/*@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append(", mail=");
		builder.append(mail);
		builder.append("]");
		return builder.toString();
	}*/
}
