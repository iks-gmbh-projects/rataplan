package iks.surveytool.services;

import java.security.PublicKey;

public interface CryptoService {
    String encryptDB(String raw) throws CryptoException;
    String decryptDB(String encrypted) throws CryptoException;
    PublicKey authIdKey();
}
