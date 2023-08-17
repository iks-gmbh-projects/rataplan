package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

import java.security.PrivateKey;
import java.security.PublicKey;

public class MockCryptoService implements CryptoService {
    @Override
    public String encryptDB(String raw) throws CryptoException {
        return raw;
    }

    @Override
    public String decryptDB(String encrypted) throws CryptoException {
        return encrypted;
    }

    @Override
    public PublicKey authIdKey() {
        return null;
    }

    @Override
    public PublicKey getPublicKey() {
        return null;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return null;
    }
}
