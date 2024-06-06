package de.iks.rataplan.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.Base64;

@Data
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties("keys.db")
public class DbKeyConfig {
    private String algorithm = "AES";
    private String key = "";
    private Path path;
    
    public Key resolveKey() throws IOException {
        byte[] bytes;
        if (key.isEmpty()) {
            bytes = Files.readAllBytes(path);
        } else bytes = Base64.getDecoder().decode(key);
        return new SecretKeySpec(bytes, algorithm);
    }
}