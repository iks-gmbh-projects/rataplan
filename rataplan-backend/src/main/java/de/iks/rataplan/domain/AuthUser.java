package de.iks.rataplan.domain;

import lombok.*;

@Data
@NoArgsConstructor
public class AuthUser {
	
	private Integer id;
	private String mail;
	private String username;
    private String password;
	private String displayname;
    
//    public AuthUser(String mail, String username, String password, String firstName, String lastName) {
//		this.mail = mail;
//		this.username = username;
//		this.password = password;
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.trimUserCredentials();
//	}
    
    public AuthUser(Integer id, String mail, String username, String password, String displayname) {
    	this.id = id;
 		this.mail = mail;
 		this.username = username;
 		this.password = password;
		this.displayname = displayname;
 		this.trimUserCredentials();
 	}
    
    public AuthUser(FrontendUser frontendUser) {
    	this.mail = frontendUser.getMail();
    	this.username = frontendUser.getUsername();
    	this.password = frontendUser.getPassword();
		this.displayname = frontendUser.getDisplayname();
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

	public void trimUserCredentials() {
		username = trimAndNull(username);
		mail = trimAndNull(mail);
	}
	
	public String trimAndNull(String toTrim) {
		if (toTrim != null) {
			toTrim = toTrim.trim();
			if (toTrim.equals("")) {
				return null;
			}
		}
		return toTrim;
	}
	
}
