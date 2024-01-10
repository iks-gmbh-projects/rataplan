package de.iks.rataplan.mapping.crypto;

import de.iks.rataplan.domain.EncryptedString;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ToEncryptedStringConverter extends AbstractConverter<String, EncryptedString> {
    private final CryptoService cryptoService;
    @Override
    public EncryptedString convert(String source) {
        if(source == null) return null;
        return new EncryptedString(cryptoService.encryptDB(source), true);
    }
}
