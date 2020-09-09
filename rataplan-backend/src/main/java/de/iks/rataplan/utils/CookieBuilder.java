package de.iks.rataplan.utils;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CookieBuilder {

	private static final String JWT_TOKEN = "jwttoken";
	
	@Autowired
	private Environment env;
	
	public Cookie createJWTCookie(String token, boolean logout) {
		Cookie cookie = new Cookie(JWT_TOKEN, token);
		cookie.setDomain(env.getProperty("rataplan.backend.domain"));
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(60000);
		
		if ("true".equals(env.getProperty("RATAPLAN.PROD"))) {
			cookie.setSecure(true);
		}
		
		if (!logout) {
			cookie.setMaxAge(60000);
		} else {
			cookie.setMaxAge(0);
		}
		return cookie;
	}
}
