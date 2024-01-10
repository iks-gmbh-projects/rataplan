package iks.surveytool.mapping.crypto;

import iks.surveytool.entities.EncryptedString;
import lombok.extern.log4j.Log4j2;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
@Log4j2
public class DBEncryptedStringConverter implements AttributeConverter<EncryptedString, String> {
    private static final String ENC = "ENC__##__", RAW = "RAW__##__";
    @Override
    public String convertToDatabaseColumn(EncryptedString attribute) {
        if(attribute == null) return null;
        return (attribute.isEncrypted() ? ENC : RAW) + attribute.getString();
    }
    
    @Override
    public EncryptedString convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        if(dbData.startsWith(ENC)) {
            return new EncryptedString(dbData.substring(ENC.length()), true);
        } else if(dbData.startsWith(RAW)) {
            return new EncryptedString(dbData.substring(RAW.length()), false);
        } else {
            log.warn("DB Entry without prefix found");
            log.warn("Treating as RAW");
            return new EncryptedString(dbData, false);
        }
    }
}
