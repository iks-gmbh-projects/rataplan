package de.iks.rataplan.controller;

import lombok.RequiredArgsConstructor;

import com.nimbusds.jose.jwk.JWKSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class JwtIssuerController {
    private final JWKSet jwks;
    
    @GetMapping(value = "/jwks", produces = JWKSet.MIME_TYPE)
    public String jwks() {
        return jwks.toString();
    }
}