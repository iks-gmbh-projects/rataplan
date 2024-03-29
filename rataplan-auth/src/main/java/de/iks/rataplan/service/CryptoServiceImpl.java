package de.iks.rataplan.service;

import de.iks.rataplan.config.IDKeyConfig;
import de.iks.rataplan.config.DBKeyConfig;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.CryptoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoServiceImpl implements CryptoService {
    private final DBKeyConfig db;
    private final IDKeyConfig id;
    private Key dbKey;
    private KeyPair idKey;
    
    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException {
        {
            log.info("Loading DB key");
            byte[] bytes;
            final String encKey = db.getKey();
            if(encKey == null) {
                bytes = Files.readAllBytes(Paths.get(db.getPath()));
            } else bytes = Base64.getDecoder().decode(encKey);
            dbKey = new SecretKeySpec(bytes, db.getAlgorithm());
            log.info("DB key loaded");
        }
        {
            log.info("Loading ID s");
            KeyPairGenerator gen = KeyPairGenerator.getInstance(id.getAlgorithm());
            gen.initialize(id.getLength());
            idKey = gen.generateKeyPair();
            log.info("ID keys loaded");
        }
    }
    @Override
    public byte[] decryptDBRaw(String raw) throws CryptoException {
        if(raw == null) return null;
        try {
            final Cipher cipher = Cipher.getInstance(dbKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, dbKey);
            return cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
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
        return Base64.getEncoder().encodeToString(decryptDBRaw(raw));
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
    public User ensureEncrypted(User user) throws CryptoException {
        if(user != null && !user.isEncrypted()) {
            user.setMail(encryptDB(user.getMail()));
            user.setUsername(encryptDB(user.getUsername()));
            user.setDisplayname(encryptDB(user.getDisplayname()));
            user.setEncrypted(true);
        }
        return user;
    }
    
    @Override
    public PublicKey idKey() {
        return idKey.getPublic();
    }
    
    @Override
    public PrivateKey idKeyP() {
        return idKey.getPrivate();
    }
}
