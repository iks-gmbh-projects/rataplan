package de.iks.rataplan.mapping.crypto;

import de.iks.rataplan.domain.EncryptedString;
import de.iks.rataplan.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FromEncryptedStringConverter extends AbstractConverter<EncryptedString, String> {
    private final CryptoService cryptoService;
    @Override
    public String convert(EncryptedString source) {
        if(source == null) return null;
        if(source.isEncrypted()) {
            return cryptoService.decryptDB(source.getString());
        } else {
            return source.getString();
        }
    }
}
