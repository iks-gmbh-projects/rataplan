package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoService {
    String encryptDB(String raw) throws CryptoException;
    String decryptDB(String encrypted) throws CryptoException;
    PublicKey getAuthIdKey();
    PublicKey getAuthIdKey(long since);
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
}
