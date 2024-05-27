package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

import java.nio.charset.StandardCharsets;

public class MockCryptoService implements CryptoService {
    @Override
    public byte[] encryptDBRaw(String str) throws CryptoException {
        return str.getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public String decryptDBRaw(byte[] encrypted) throws CryptoException {
        return new String(encrypted, StandardCharsets.UTF_8);
    }
    @Override
    public String encryptDB(String raw) throws CryptoException {
        return raw;
    }
    
    @Override
    public String decryptDB(String encrypted) throws CryptoException {
        return encrypted;
    }
}