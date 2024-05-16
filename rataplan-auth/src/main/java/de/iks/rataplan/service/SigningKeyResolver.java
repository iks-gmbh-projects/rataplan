package de.iks.rataplan.service;

import de.iks.rataplan.config.BackendMessageConfig;
import de.iks.rataplan.dto.PublicKeyExchangeDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Service
@RequiredArgsConstructor
public class SigningKeyResolver implements io.jsonwebtoken.SigningKeyResolver {
    private final BackendMessageConfig messageConfig;
    private final RestTemplate restTemplate;
    private long fetchTime;
    private PublicKey backendPublicKey;
    
    @SneakyThrows
    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        if(backendPublicKey == null || fetchTime > claims.getIssuedAt().getTime()) {
            PublicKeyExchangeDTO publicKeyExchangeDTO = restTemplate.getForEntity(
                messageConfig.getPublicKey(),
                PublicKeyExchangeDTO.class
            ).getBody();
            if(publicKeyExchangeDTO == null) return backendPublicKey;
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyExchangeDTO.getEncodedKey());
            this.backendPublicKey = KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
            this.fetchTime = System.currentTimeMillis();
        }
        return backendPublicKey;
    }
    
    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        return null;
    }
}