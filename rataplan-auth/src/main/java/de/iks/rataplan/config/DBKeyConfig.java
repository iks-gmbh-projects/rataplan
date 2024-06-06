package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties("keys.db")
@Data
public class DBKeyConfig {
    private String algorithm = "AES";
    private String key;
    private Path path;
}