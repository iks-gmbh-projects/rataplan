package de.iks.rataplan.utils;

import de.iks.rataplan.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CookieBuilder {

    private static final String JWT_TOKEN = "jwttoken";

    @Autowired
    private Environment env;

    @Autowired
    private JwtTokenService tokenService;

    public String generateCookieValue(String token, boolean logout) {
        long secondsUntilExpiration;
        if(logout) secondsUntilExpiration = 0;
        else {
            long millisUntilExpiration = tokenService.getTokenExpiration(token).getTime() -new Date().getTime();
            secondsUntilExpiration = millisUntilExpiration / 1000;
        }
        return JWT_TOKEN + "=" + token + "; Max-Age=" + secondsUntilExpiration + "; Path=/; " + ("true".equals(env.getProperty("RATAPLAN.PROD")) ? "Secure" : "") + "; HttpOnly; SameSite=strict";
    }
}
