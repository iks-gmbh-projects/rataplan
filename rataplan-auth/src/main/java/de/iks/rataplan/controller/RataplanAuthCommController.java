package de.iks.rataplan.controller;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.NotificationDTO;
import de.iks.rataplan.service.NotificationService;
import de.iks.rataplan.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final UserService userService;
    private final NotificationService notificationService;
    
    @GetMapping("/userid")
    public ResponseEntity<?> getIdByEmail(@RequestParam String email) {
        User user = userService.getUserFromEmail(email);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user.getId());
    }
    
    @PostMapping("/notification")
    public ResponseEntity<Boolean> notifyUsers(
        @RequestBody List<NotificationDTO> notifications
    ) {
        notificationService.notify(notifications);
        return ResponseEntity.ok(true);
    }
}