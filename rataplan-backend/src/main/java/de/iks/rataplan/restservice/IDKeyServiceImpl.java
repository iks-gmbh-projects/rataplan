package de.iks.rataplan.restservice;

import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.exceptions.RataplanException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Service
@RequiredArgsConstructor
public class IDKeyServiceImpl implements IDKeyService {
    private final KeyExchangeConfig keyExchangeConfig;
    private final RestTemplate restTemplate;
    private PublicKey key = null;
    private long fetchTime = 0;
    @Override
    public PublicKey getIDKey(boolean signatureFailure) {
        if(key == null ||
            (fetchTime + keyExchangeConfig.getCachetime()*1000 < System.currentTimeMillis() &&
                (signatureFailure || keyExchangeConfig.isShortenedCache())
            )
        ) {
            ResponseEntity<KeyDTO> response = restTemplate.getForEntity(keyExchangeConfig.getUrl(), KeyDTO.class);
            if(!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) throw new RataplanException("Key fetch error");
            KeyDTO dto = response.getBody();
            try {
                KeyFactory factory = KeyFactory.getInstance(dto.getAlgorithm());
                key = factory.generatePublic(new X509EncodedKeySpec(dto.getEncoded()));
                fetchTime = System.currentTimeMillis();
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new RataplanException("Key decode error", ex);
            }
        }
        return key;
    }
}
