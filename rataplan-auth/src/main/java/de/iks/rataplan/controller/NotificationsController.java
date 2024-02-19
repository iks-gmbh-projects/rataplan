package de.iks.rataplan.controller;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.dto.NotificationSettingsDTO;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.exceptions.RataplanAuthException;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.NotificationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static de.iks.rataplan.controller.RataplanAuthRestController.JWT_COOKIE_NAME;

@RequiredArgsConstructor
@Controller
@RequestMapping("/v1/notifications")
public class NotificationsController {
    private final JwtTokenService jwtTokenService;
    private final NotificationService notificationService;
    private String validateTokenOrThrow(String cookieToken, String headerToken) throws RataplanAuthException {
        String token;
        if(headerToken == null) token = cookieToken;
        else if(cookieToken == null) token = headerToken;
        else if(cookieToken.equals(headerToken)) token = cookieToken;
        else throw new RataplanAuthException("Different tokens provided by cookie and header.");
        if(!jwtTokenService.isTokenValid(token)) {
            throw new InvalidTokenException("Invalid token");
        }
        return token;
    }
    
    @GetMapping("/list-settings")
    public ResponseEntity<Map<String, List<String>>> listSettings() {
        return ResponseEntity.ok(notificationService.getNotificationTypes());
    }
    
    @GetMapping("/options")
    public ResponseEntity<List<EmailCycle>> listOptions() {
        return ResponseEntity.ok(List.of(EmailCycle.values()));
    }
    
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingsDTO> getSettings(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        return ResponseEntity.ok(notificationService.getNotificationSettings(jwtTokenService.getUserIdFromToken(token)));
    }
    
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsDTO> getSettings(
        @RequestHeader(value = JWT_COOKIE_NAME, required = false) String tokenHeader,
        @CookieValue(value = JWT_COOKIE_NAME, required = false) String tokenCookie,
        @RequestBody NotificationSettingsDTO settings
    )
    {
        String token = validateTokenOrThrow(tokenCookie, tokenHeader);
        return ResponseEntity.ok(notificationService.updateNotificationSettings(
            jwtTokenService.getUserIdFromToken(token),
            settings
        ));
    }
}
