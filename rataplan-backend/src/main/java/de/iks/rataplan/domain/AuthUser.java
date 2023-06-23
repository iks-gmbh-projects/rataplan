package de.iks.rataplan.domain;

import lombok.*;

@Data
@NoArgsConstructor
public class AuthUser {
	
	private Integer id;
	private String username;
    
    public AuthUser(Integer id, String username) {
    	this.id = id;
 		this.username = username;
 		this.trimUserCredentials();
 	}

	public void trimUserCredentials() {
		username = trimAndNull(username);
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
