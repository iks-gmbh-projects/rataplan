package de.iks.rataplan.service;

import de.iks.rataplan.exceptions.CryptoException;

public interface CryptoService {
    byte[] encryptDBRaw(String str) throws CryptoException;
    String encryptDB(String raw) throws CryptoException;
    String decryptDBRaw(byte[] encrypted) throws CryptoException;
    String decryptDB(String encrypted) throws CryptoException;
}