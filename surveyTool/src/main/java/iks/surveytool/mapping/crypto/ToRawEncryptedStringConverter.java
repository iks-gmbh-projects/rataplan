package iks.surveytool.mapping.crypto;

import iks.surveytool.services.CryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToRawEncryptedStringConverter extends AbstractConverter<String, byte[]> {
    private final CryptoService cryptoService;
    @Override
    protected byte[] convert(String source) {
        return cryptoService.encryptDBRaw(source);
    }
}