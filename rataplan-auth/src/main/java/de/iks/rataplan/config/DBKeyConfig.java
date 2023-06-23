package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("keys.db")
@Data
public class DBKeyConfig {
    private String algorithm = "AES";
    private String key;
    private String path;
}
