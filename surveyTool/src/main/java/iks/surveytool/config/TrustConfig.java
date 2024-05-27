package iks.surveytool.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("trusted")
@Data
public class TrustConfig {
    private String issuers = "^$";
}