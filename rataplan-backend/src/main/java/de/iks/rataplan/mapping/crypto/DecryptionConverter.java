package de.iks.rataplan.mapping.crypto;

import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecryptionConverter extends AbstractConverter<byte[], String> {
    private final CryptoService cryptoService;
    @Override
    protected String convert(byte[] s) {
        return cryptoService.decryptDBRaw(s);
    }
}
