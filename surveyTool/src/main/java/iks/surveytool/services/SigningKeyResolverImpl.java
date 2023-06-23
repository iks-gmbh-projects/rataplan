package iks.surveytool.services;

import iks.surveytool.config.KeyExchangeConfig;
import iks.surveytool.dtos.KeyDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Service
@RequiredArgsConstructor
public class SigningKeyResolverImpl implements SigningKeyResolver {
    private final KeyExchangeConfig keyExchangeConfig;
    private final RestTemplate restTemplate;
    private PublicKey key = null;
    private long fetchTime = 0;
    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        if(key == null || claims.getIssuedAt().getTime() > fetchTime) {
            ResponseEntity<KeyDTO> response = restTemplate.getForEntity(keyExchangeConfig.getUrl(), KeyDTO.class);
            if(!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) throw new CryptoException("Key exchange error", null);
            KeyDTO dto = response.getBody();
            try {
                KeyFactory factory = KeyFactory.getInstance(dto.getAlgorithm());
                key = factory.generatePublic(new X509EncodedKeySpec(dto.getEncoded()));
                fetchTime = System.currentTimeMillis();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new CryptoException("Key decode error", ex);
            }
        }
        return key;
    }
    
    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, String s) {
        return null;
    }
}
