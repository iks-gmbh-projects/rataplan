package de.iks.rataplan.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;

@Component
@RequiredArgsConstructor
public class CookieBuilder {
	private static final String JWT_TOKEN = "jwttoken";
	
	private final Environment env;

	public String generateCookieValue(String token, boolean logout) {
		return JWT_TOKEN + "=" + token + "; Max-Age=" + (logout ? 0 : 60000) + "; Domain=" + env.getProperty("rataplan.backend.domain") + "; Path=/; " + ("true".equals(env.getProperty("RATAPLAN.PROD")) ? "Secure" : "") + "; HttpOnly; SameSite=" + ("true".equals(env.getProperty("RATAPLAN.PROD")) ? "none" : "strict");
	}

	public Cookie generateCookie(String token, boolean logout) {
		Cookie cookie = new Cookie(JWT_TOKEN, token);
		cookie.setMaxAge(logout ? 0 : 60000);
		cookie.setDomain(env.getProperty("rataplan.backend.domain"));
		cookie.setSecure("true".equals(env.getProperty("RATAPLAN.PROD")));
		cookie.setHttpOnly(true);
		return cookie;
	}
}
