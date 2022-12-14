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
public class ToEncryptedStringConverter extends AbstractConverter<String, EncryptedString> {
    private final CryptoService cryptoService;
    @Override
    public EncryptedString convert(String source) {
        if(source == null) return null;
        return new EncryptedString(cryptoService.encryptDB(source), true);
    }
}
