package de.iks.rataplan.utils;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;

@Component
public class CookieBuilder {

	private static final String JWT_TOKEN = "jwttoken";
	
	@Autowired
	private Environment env;

	public String generateCookieValue(String token, boolean logout) {
		return JWT_TOKEN + "=" + token + "; Max-Age=" + (logout ? 0 : 60000) + "; Domain=" + env.getProperty("rataplan.backend.domain") + "; Path=/; " + ("true".equals(env.getProperty("RATAPLAN.PROD")) ? "Secure" : "") + "; HttpOnly; SameSite=" + ("true".equals(env.getProperty("RATAPLAN.PROD")) ? "none" : "strict");
	}
}
