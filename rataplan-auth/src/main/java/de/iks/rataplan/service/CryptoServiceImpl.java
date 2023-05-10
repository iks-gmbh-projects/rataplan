package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.CryptoException;
import org.springframework.beans.factory.annotation.Value;
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
public class CryptoServiceImpl implements CryptoService {
    private final String dbKeyAlgorithm;
    private final String dbKeyBytes;
    private final String dbKeyPath;

    private Key dbKey;

    public CryptoServiceImpl(
        @Value("${keys.db.algorithm:AES}") String dbKeyAlgorithm,
        @Value("${keys.db.key:}") String dbKeyBytes,
        @Value("${keys.db.path:}") String dbKeyPath
    ) {
        this.dbKeyAlgorithm = dbKeyAlgorithm;
        this.dbKeyBytes = dbKeyBytes;
        this.dbKeyPath = dbKeyPath;
    }

    @PostConstruct
    public void init() throws IOException {
        byte[] bytes;
        if (dbKeyBytes.isEmpty()) {
            bytes = Files.readAllBytes(Paths.get(dbKeyPath));
        } else bytes = Base64.getDecoder().decode(dbKeyBytes);
        dbKey = new SecretKeySpec(bytes, dbKeyAlgorithm);
    }

    @Override
    public String encryptDB(String raw) throws CryptoException {
        if (raw == null) return null;
        try {
            final Cipher cipher = Cipher.getInstance(dbKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, dbKey);
            return Base64.getEncoder().encodeToString(
                cipher.doFinal(
                    raw.getBytes(StandardCharsets.UTF_8)
                )
            );
        } catch (InvalidKeyException |
                 NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 IllegalBlockSizeException |
                 BadPaddingException ex) {
            throw new CryptoException(ex);
        }
    }

    @Override
    public String decryptDB(String encrypted) throws CryptoException {
        if (encrypted == null) return null;
        try {
            final Cipher cipher = Cipher.getInstance(dbKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, dbKey);
            return new String(
                cipher.doFinal(
                    Base64.getDecoder().decode(encrypted)
                ),
                StandardCharsets.UTF_8
            );
        } catch (InvalidKeyException |
                 NoSuchAlgorithmException |
                 NoSuchPaddingException |
                 IllegalBlockSizeException |
                 BadPaddingException ex) {
            throw new CryptoException(ex);
        }
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
        return null;
    }

    @Override
    public PrivateKey idKeyP() {
        return null;
    }
}
