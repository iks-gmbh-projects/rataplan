package de.iks.rataplan.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.url")
@Data
@RequiredArgsConstructor
public class AuthBackendUrlConfig {
    private String base;
    private String displayName;
    private String email;
    private String notification;
}