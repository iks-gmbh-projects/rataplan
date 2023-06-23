package de.iks.rataplan.controller;

import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class RataplanAuthCommController {
    private final CryptoService cryptoService;
    @GetMapping("/pubid")
    public KeyDTO getIDKey() {
        PublicKey idKey = cryptoService.idKey();
        return new KeyDTO(idKey.getAlgorithm(), idKey.getEncoded());
    }
}
