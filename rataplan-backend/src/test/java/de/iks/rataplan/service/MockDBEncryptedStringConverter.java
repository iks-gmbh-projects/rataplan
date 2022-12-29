package de.iks.rataplan.service;

import de.iks.rataplan.domain.EncryptedString;
import de.iks.rataplan.mapping.crypto.DBEncryptedStringConverter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MockDBEncryptedStringConverter extends DBEncryptedStringConverter {
    @Override
    public String convertToDatabaseColumn(EncryptedString attribute) {
        return attribute.getString();
    }

    @Override
    public EncryptedString convertToEntityAttribute(String dbData) {
        return new EncryptedString(dbData, true);
    }
}
