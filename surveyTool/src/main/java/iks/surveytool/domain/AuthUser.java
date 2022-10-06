package iks.surveytool.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
	
	private Long id;
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
		lastName = trimAndNull(lastName);
		firstName = trimAndNull(firstName);
	}
	
	public String trimAndNull(String toTrim) {
		if (toTrim != null) {
			toTrim = toTrim.trim();
			if (toTrim.isEmpty()) {
				return null;
			}
		}
		return toTrim;
	}
	
	@Override
	public String toString() {
		return "User [username=" +
				username +
				", password=" +
				password +
				", mail=" +
				mail +
				"]";
	}

	{
		trimUserCredentials();
	}
}