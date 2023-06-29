package de.iks.rataplan.controller;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.service.CryptoService;
import de.iks.rataplan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final CryptoService cryptoService;
    private final UserService userService;
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
