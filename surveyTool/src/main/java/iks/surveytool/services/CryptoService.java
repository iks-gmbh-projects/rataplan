package iks.surveytool.services;

import java.security.PublicKey;

public interface CryptoService {
    String encryptDB(String raw) throws CryptoException;
    byte[] encryptDBRaw(String raw) throws CryptoException;
    String decryptDB(String encrypted) throws CryptoException;
    String decryptDBRaw(byte[] encrypted) throws CryptoException;
    PublicKey authIdKey();
}