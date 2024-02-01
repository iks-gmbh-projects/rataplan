package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoService {
    byte[] encryptDBRaw(String raw) throws CryptoException;
    default byte[] encryptDB(String raw) throws CryptoException {
        return encryptDBRaw(raw);
    }
    String decryptDBRaw(byte[] encrypted) throws CryptoException;
    default String decryptDB(byte[] encrypted) throws CryptoException {
        return decryptDBRaw(encrypted);
    }
    PublicKey idKey();
    PrivateKey idKeyP();
}
