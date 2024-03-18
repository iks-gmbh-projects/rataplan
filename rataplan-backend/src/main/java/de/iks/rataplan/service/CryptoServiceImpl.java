package de.iks.rataplan.service;

import de.iks.rataplan.config.DbKeyConfig;
import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.dto.KeyDTO;
import de.iks.rataplan.exceptions.CryptoException;
import de.iks.rataplan.exceptions.RataplanException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class CryptoServiceImpl implements CryptoService {
    private final DbKeyConfig dbKeyConfig;
    
    private final KeyExchangeConfig keyExchangeConfig;
    private final RestTemplate restTemplate;
    private PublicKey authIdKey = null;
    private long fetchTime = 0;
    
    private Key dbKey;
    private KeyPair keyPair;
    
    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException {
        this.dbKey = dbKeyConfig.resolveKey();
        this.keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
    
    @Override
    public byte[] encryptDBRaw(String str) throws CryptoException {
        if(str == null) return null;
        try {
            final Cipher cipher = Cipher.getInstance(dbKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, dbKey);
            return cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
        } catch(InvalidKeyException |
            NoSuchAlgorithmException |
            NoSuchPaddingException |
            IllegalBlockSizeException |
            BadPaddingException ex) {
            throw new CryptoException(ex);
        }
    }
    @Override
    public String encryptDB(String raw) throws CryptoException {
        if(raw == null) return null;
        return Base64.getEncoder().encodeToString(encryptDBRaw(raw));
    }
    
    @Override
    public String decryptDBRaw(byte[] encrypted) throws CryptoException {
        if(encrypted == null) return null;
        try {
            final Cipher cipher = Cipher.getInstance(dbKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, dbKey);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch(InvalidKeyException |
            NoSuchAlgorithmException |
            NoSuchPaddingException |
            IllegalBlockSizeException |
            BadPaddingException ex) {
            throw new CryptoException(ex);
        }
    }
    @Override
    public String decryptDB(String encrypted) throws CryptoException {
        if(encrypted == null) return null;
        return decryptDBRaw(Base64.getDecoder().decode(encrypted));
    }
    
    @Override
    public PublicKey getAuthIdKey() {
        if(authIdKey == null) {
            ResponseEntity<KeyDTO> response = restTemplate.getForEntity(keyExchangeConfig.getUrl(), KeyDTO.class);
            if(!response.getStatusCode().is2xxSuccessful() || !response.hasBody())
                throw new RataplanException("Key fetch error");
            KeyDTO dto = response.getBody();
            if(dto == null) throw new RataplanException("Key fetch error");
            try {
                KeyFactory factory = KeyFactory.getInstance(dto.getAlgorithm());
                authIdKey = factory.generatePublic(new X509EncodedKeySpec(dto.getEncoded()));
                fetchTime = System.currentTimeMillis();
            } catch(NoSuchAlgorithmException | InvalidKeySpecException ex) {
                throw new RataplanException("Key decode error", ex);
            }
        }
        return authIdKey;
    }
    @Override
    public PublicKey getAuthIdKey(long since) {
        if(since > fetchTime) this.authIdKey = null;
        return getAuthIdKey();
    }
    @Override
    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }
    
    @Override
    public PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }
}
