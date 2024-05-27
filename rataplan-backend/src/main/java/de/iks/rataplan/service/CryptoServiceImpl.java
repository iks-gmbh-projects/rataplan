package de.iks.rataplan.service;

import de.iks.rataplan.config.DbKeyConfig;
import de.iks.rataplan.exceptions.CryptoException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@RequiredArgsConstructor
@Service
public class CryptoServiceImpl implements CryptoService {
    private final DbKeyConfig dbKeyConfig;
    
    private Key dbKey;
    
    @PostConstruct
    public void init() throws IOException {
        this.dbKey = dbKeyConfig.resolveKey();
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
}