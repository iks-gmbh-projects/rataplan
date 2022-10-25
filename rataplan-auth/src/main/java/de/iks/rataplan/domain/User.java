package de.iks.rataplan.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@Entity
@Table(name = "rataplanuser")
@ToString
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1171464424149123656L;

    private Integer id;
    private String mail;
    private String username;
    private String password;
    private String displayname;

    public User(Integer id, String mail, String username, String password, String displayname) {
        this.id = id;
        this.mail = mail;
        this.username = username;
        this.password = password;
        this.displayname = displayname;
    }

    public User() {
        // Required for Hibernate
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

    public String trimAndNull(String toTrim) {
        if (toTrim != null) {
            toTrim = toTrim.trim();
            if (toTrim == "") {
                return null;
            }
        }
        return toTrim;
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
