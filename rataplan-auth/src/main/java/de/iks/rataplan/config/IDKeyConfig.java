package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("keys.id")
@Data
public class IDKeyConfig {
    private String algorithm = "RSA";
    private int length = 2048;
}
