package de.iks.rataplan.mapping.crypto;

import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptionConverter extends AbstractConverter<String, byte[]> {
    private final CryptoService cryptoService;
    @Override
    protected byte[] convert(String s) {
        return cryptoService.encryptDBRaw(s);
    }
}
