package iks.surveytool.mapping.crypto;

import iks.surveytool.entities.EncryptedString;
import iks.surveytool.services.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class FromEncryptedStringConverter extends AbstractConverter<EncryptedString, String> {
    private final CryptoService cryptoService;
    @Override
    protected String convert(EncryptedString source) {
        if(source == null) return null;
        if(source.isEncrypted()) {
            return cryptoService.decryptDB(source.getString());
        } else {
            return source.getString();
        }
    }
}
