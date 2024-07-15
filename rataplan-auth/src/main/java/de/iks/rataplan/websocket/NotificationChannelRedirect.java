package de.iks.rataplan.websocket;

import de.iks.rataplan.service.JwtTokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class NotificationChannelRedirect implements ChannelRedirect {
    private static final Pattern NOTIFICATION_PATTERN = Pattern.compile("/notifications(?:/\\d++)?+");
    private final JwtTokenService jwtTokenService;
    @Override
    public boolean matches(String channel) {
        return NOTIFICATION_PATTERN.asMatchPredicate().test(channel);
    }
    @Override
    public String redirect(String original, Jwt token) {
        return "/notifications/" + jwtTokenService.getUserId(token);
    }
}
