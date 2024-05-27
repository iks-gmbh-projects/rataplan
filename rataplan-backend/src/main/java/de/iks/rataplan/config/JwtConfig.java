package de.iks.rataplan.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ConfigurationProperties("token")
@Data
@Slf4j
public class JwtConfig {
    private long lifetime = 10;
    private String issuer = "https://backend.drumdibum.de/issuer";
    
    @Bean
    @Qualifier("jwkSet")
    public JWKSet jwkSet(List<? extends JWKGenerator<?>> generators) {
        List<JWK> jwks = generators.parallelStream().flatMap(g -> {
            try {
                return Stream.of(g.generate());
            } catch(JOSEException ex) {
                log.warn("Error during key generation: ", ex);
                return Stream.empty();
            }
        }).collect(Collectors.toUnmodifiableList());
        if(jwks.isEmpty()) throw new NoSuchElementException("No signing keys could be generated");
        return new JWKSet(jwks);
    }
    
    @Bean
    public JwtEncoder jwtEncoder(@Qualifier("jwkSet") JWKSet jwkSet) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(jwkSet));
    }
}