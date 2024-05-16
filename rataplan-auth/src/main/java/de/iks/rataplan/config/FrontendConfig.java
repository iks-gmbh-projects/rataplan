package de.iks.rataplan.config;

import lombok.Data;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("rataplan.frontend")
@RequiredArgsConstructor
@Data
public class FrontendConfig {
    private String url;
}