package iks.surveytool.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
	
	private Long id;
	private String username;

	public void trimUserCredentials() {
		username = trimAndNull(username);
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

	{
		trimUserCredentials();
	}
}