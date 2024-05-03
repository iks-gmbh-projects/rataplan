package de.iks.rataplan.service;

import de.iks.rataplan.dto.PublicKeyExchangeDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

@Service
public class SigningKeyResolver implements io.jsonwebtoken.SigningKeyResolver {

    private long fetchTime;
    private PublicKey backendPublicKey;
    @Value("${backend.appointment.urltemplate.public.key}")
    private String backendPublicKeyUrl;

    @SneakyThrows
    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        if (backendPublicKey == null || fetchTime > claims.getIssuedAt().getTime()) {
            RestTemplate restTemplate = new RestTemplate();
            PublicKeyExchangeDTO publicKeyExchangeDTO = restTemplate.getForEntity(backendPublicKeyUrl, PublicKeyExchangeDTO.class).getBody();
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyExchangeDTO.getEncodedKey());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
            this.backendPublicKey = publicKey;
            this.fetchTime = System.currentTimeMillis();
        }
        return backendPublicKey;
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        return null;
    }
}