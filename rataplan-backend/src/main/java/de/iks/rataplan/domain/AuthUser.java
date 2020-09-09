package de.iks.rataplan.domain;

public class AuthUser {
	
	private Integer id;
	private String mail;
	private String username;
    private String password;
    private String firstName;
    private String lastName;
    
//    public AuthUser(String mail, String username, String password, String firstName, String lastName) {
//		this.mail = mail;
//		this.username = username;
//		this.password = password;
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.trimUserCredentials();
//	}
    
    public AuthUser(Integer id, String mail, String username, String password, String firstName, String lastName) {
    	this.id = id;
 		this.mail = mail;
 		this.username = username;
 		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
 		this.trimUserCredentials();
 	}
    
    public AuthUser(FrontendUser frontendUser) {
    	this.mail = frontendUser.getMail();
    	this.username = frontendUser.getUsername();
    	this.password = frontendUser.getPassword();
		this.firstName = frontendUser.getFirstname();
		this.lastName = frontendUser.getLastname();
    	this.trimUserCredentials();
    }
    
//    public AuthUser(FrontendUser frontendUser, int authUserId) {
//    	this.id = authUserId;
//    	this.mail = frontendUser.getMail();
//    	this.username = frontendUser.getUsername();
//    	this.password = frontendUser.getPassword();
//		this.firstName = frontendUser.getFirstname();
//		this.lastName = frontendUser.getLastname();
//    	this.trimUserCredentials();
//    }
    
	public AuthUser() {
    	// Required for Hibernate
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
	
	public void setUsername(String name) {
		this.username = name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void trimUserCredentials() {
		username = trimAndNull(username);
		mail = trimAndNull(mail);
		lastName = trimAndNull(lastName);
		firstName = trimAndNull(firstName);
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
	
	@Override
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
	}
	
}