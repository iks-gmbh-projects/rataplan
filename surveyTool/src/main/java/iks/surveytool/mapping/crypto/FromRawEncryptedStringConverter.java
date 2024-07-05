package iks.surveytool.mapping.crypto;

import iks.surveytool.services.CryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FromRawEncryptedStringConverter extends AbstractConverter<byte[], String> {
    private final CryptoService cryptoService;
    @Override
    protected String convert(byte[] source) {
        return cryptoService.decryptDBRaw(source);
    }
}