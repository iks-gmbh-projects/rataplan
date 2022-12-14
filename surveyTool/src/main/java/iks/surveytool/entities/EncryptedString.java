package iks.surveytool.entities;

import lombok.Data;

@Data
public class EncryptedString {
    private final String string;
    private final boolean encrypted;
}
