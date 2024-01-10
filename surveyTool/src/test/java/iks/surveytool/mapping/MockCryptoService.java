package iks.surveytool.mapping;

import iks.surveytool.services.CryptoException;
import iks.surveytool.services.CryptoService;

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
}
