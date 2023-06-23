package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("token")
@Data
public class JwtConfig {
    private long lifetime = 60000;
    private String issuer = "drumdibum-auth";
}
