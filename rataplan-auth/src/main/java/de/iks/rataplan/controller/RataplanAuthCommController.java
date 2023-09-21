package de.iks.rataplan.controller;

import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.service.CryptoService;
import de.iks.rataplan.service.JwtTokenService;
import de.iks.rataplan.service.MailService;
import de.iks.rataplan.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final CryptoService cryptoService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final MailService mailServiceImplSendInBlue;
    
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
}