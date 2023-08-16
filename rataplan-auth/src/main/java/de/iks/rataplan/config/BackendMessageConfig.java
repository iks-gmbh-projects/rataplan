package de.iks.rataplan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("backend.vote.urltemplate")
@Data
public class BackendMessageConfig {
    private String delete;
    private String anonymize;
    private String onRegister;
}
