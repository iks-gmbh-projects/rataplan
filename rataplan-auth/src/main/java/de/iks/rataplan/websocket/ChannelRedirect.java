package de.iks.rataplan.websocket;

import org.springframework.security.oauth2.jwt.Jwt;

public interface ChannelRedirect {
    boolean matches(String channel);
    String redirect(String original, Jwt token);
}
