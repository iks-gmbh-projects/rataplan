package de.iks.rataplan.controller;

import de.iks.rataplan.domain.ParticipantDeletionMailData;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.service.*;
import jdk.nashorn.internal.parser.JSONParser;
import de.iks.rataplan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sendinblue.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final CryptoService cryptoService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;
    private final MailService mailServiceImplSendInBlue;

    @Value("${backend.appointment.urltemplate.public.key}")
    private String backendPublicKeyUrl;
    private RestTemplate restTemplate;

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
}

    @PostMapping("/notify-participant/delete")
    public ResponseEntity<Boolean> notifyParticipant(@RequestHeader String jwt, @RequestBody ParticipantDeletionMailData participantDeletionMailData) throws IOException, ApiException {
        Integer userId = jwtTokenService.getUserIdFromBackendToken(jwt);
        String email = userService.getEmailFromId(userId);
        participantDeletionMailData.setEmail(email);
        this.mailServiceImplSendInBlue.notifyParticipantDeletion(participantDeletionMailData);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}