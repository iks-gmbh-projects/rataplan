package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.CryptoException;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface CryptoService {
    String encryptDB(String raw) throws CryptoException;
    String decryptDB(String encrypted) throws CryptoException;
    User ensureDecrypted(User user) throws CryptoException;
    User ensureEncrypted(User encrypted) throws CryptoException;
    PublicKey idKey();
    PrivateKey idKeyP();
}
