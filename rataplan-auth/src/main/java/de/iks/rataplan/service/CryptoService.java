package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

public interface CryptoService {
    byte[] encryptDBRaw(String raw) throws CryptoException;
    default byte[] encryptDB(String raw) throws CryptoException {
        return encryptDBRaw(raw);
    }
    String decryptDBRaw(byte[] encrypted) throws CryptoException;
    default String decryptDB(byte[] encrypted) throws CryptoException {
        return decryptDBRaw(encrypted);
    }
}