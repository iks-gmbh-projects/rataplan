package de.iks.rataplan.controller;

import de.iks.rataplan.domain.notifications.EmailCycle;
import de.iks.rataplan.dto.NotificationSettingsDTO;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/v1/notifications")
public class NotificationsController {
    private final JwtTokenService jwtTokenService;
    private final NotificationService notificationService;
    
    @GetMapping("/list-settings")
    public ResponseEntity<Map<String, List<String>>> listSettings() {
        return ResponseEntity.ok(notificationService.getNotificationTypes());
    }
    
    @GetMapping("/options")
    public ResponseEntity<List<EmailCycle>> listOptions() {
        return ResponseEntity.ok(List.of(EmailCycle.values()));
    }
    
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingsDTO> getSettings(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(notificationService.getNotificationSettings(jwtTokenService.getUserId(token)));
    }
    
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingsDTO> getSettings(
        @AuthenticationPrincipal Jwt token,
        @RequestBody NotificationSettingsDTO settings
    )
    {
        return ResponseEntity.ok(notificationService.updateNotificationSettings(
            jwtTokenService.getUserId(token),
            settings
        ));
    }
}