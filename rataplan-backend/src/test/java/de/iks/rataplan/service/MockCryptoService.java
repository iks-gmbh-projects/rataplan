package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

import javax.annotation.PostConstruct;
import java.security.*;

public class MockCryptoService implements CryptoService {
    private KeyPair keyPair;
    
    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        this.keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
    }
    
    @Override
    public String encryptDB(String raw) throws CryptoException {
        return raw;
    }
    
    @Override
    public String decryptDB(String encrypted) throws CryptoException {
        return encrypted;
    }
    
    @Override
    public PublicKey getAuthIdKey() {
        return null;
    }
    @Override
    public PublicKey getAuthIdKey(long since) {
        return null;
    }
    
    @Override
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
    
    @Override
    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }
}
