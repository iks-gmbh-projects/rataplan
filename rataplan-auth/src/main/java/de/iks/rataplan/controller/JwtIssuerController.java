package de.iks.rataplan.controller;

import de.iks.rataplan.dto.OpenIDAutoDiscoveryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.jwk.JWKSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/issuer")
@RequiredArgsConstructor
@Slf4j
public class JwtIssuerController {
    private final OpenIDAutoDiscoveryDTO autoDiscovery;
    private final JWKSet jwks;
    
    @GetMapping("/.well-known/openid-configuration")
    public OpenIDAutoDiscoveryDTO autoDiscovery() {
        return autoDiscovery;
    }
    
    @GetMapping(value = "/jwks", produces = JWKSet.MIME_TYPE)
    public String jwks() {
        return jwks.toString();
    }
}