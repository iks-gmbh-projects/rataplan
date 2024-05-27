package iks.surveytool.config;

import lombok.Data;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("keys.id")
@Data
public class IDKeyConfig {
    private String algorithm = "RSA";
    private int length = 2048;
    
    @Bean
    public JWKGenerator<?> generatedKey() {
        return new RSAKeyGenerator(length)
            .keyIDFromThumbprint(true)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS512);
    }
}