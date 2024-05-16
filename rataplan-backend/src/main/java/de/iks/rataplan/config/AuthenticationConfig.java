package de.iks.rataplan.config;

import lombok.Data;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("auth.id")
@Data
@RequiredArgsConstructor
public class AuthenticationConfig {
    private String issuer = "drumdibum-auth";
}