package de.iks.rataplan.controller;

import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.dto.NotificationDTO;
import de.iks.rataplan.service.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final CryptoService cryptoService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final MailService mailServiceImplSendInBlue;
    private final NotificationService notificationService;
    
    @GetMapping("/pubid")
    public KeyDTO getIDKey() {
        PublicKey idKey = cryptoService.idKey();
        return new KeyDTO(idKey.getAlgorithm(), idKey.getEncoded());
    }
    @GetMapping("/userid")
    public ResponseEntity<?> getIdByEmail(@RequestParam String email) {
        User user = userService.getUserFromEmail(email);
        if(user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user.getId());
    }
    
    @PostMapping("/notify-participant/delete")
    public ResponseEntity<Boolean> notifyParticipant(
        @RequestHeader String jwt,
        @RequestBody ParticipantDeletionMailData participantDeletionMailData
    )
    {
        Integer userId = jwtTokenService.getUserIdFromBackendToken(jwt);
        String email = userService.getEmailFromId(userId);
        participantDeletionMailData.setEmail(email);
        this.mailServiceImplSendInBlue.notifyParticipantDeletion(participantDeletionMailData);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @PostMapping("/notification")
    public ResponseEntity<Boolean> notifyUsers(
        @RequestHeader String jwt,
        @RequestBody List<NotificationDTO> notifications
    ) {
        if(!jwtTokenService.isBackendTokenValid(jwt)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        notificationService.notify(notifications);
        return ResponseEntity.ok(true);
    }
}