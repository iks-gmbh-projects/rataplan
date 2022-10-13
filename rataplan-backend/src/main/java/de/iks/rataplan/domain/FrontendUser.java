package de.iks.rataplan.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontendUser {
	
	private Integer id;
	private String mail;
	private String username;
    private String password;
	private String displayname;

	public FrontendUser(Integer id, String mail, String username, String password, String displayname) {
		this.id = id;
		this.mail = mail;
		this.username = username;
		this.password = password;
		this.displayname = displayname;
	}
	
	public FrontendUser(AuthUser authUser) {
		this.id = authUser.getId();
		this.mail = authUser.getMail();
		this.username = authUser.getUsername();
		this.password = null;
		this.displayname = authUser.getDisplayname();
	}
	
    public FrontendUser() {
		// nothing to do
    }

	public Integer getId() {
		return id;
	}
	
    public void setId(Integer id) {
		this.id = id;
	}
	
    public String getMail() {
		return mail;
	}
	
    public void setMail(String mail) {
		this.mail = mail;
	}
	
    public String getUsername() {
		return username;
	}
	
    public void setUsername(String username) {
		this.username = username;
	}
	
	@JsonIgnore
    public String getPassword() {
		return password;
	}
	
	@JsonProperty
    public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayname(){ return displayname; }

	public void setDisplayname(String displayname){ this.displayname = displayname; }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FrontendUser [id=");
		builder.append(id);
		builder.append(", mail=");
		builder.append(mail);
		builder.append(", username=");
		builder.append(username);
		builder.append(", password=");
		builder.append(password);
		builder.append("]");
		return builder.toString();
	}
    
}
