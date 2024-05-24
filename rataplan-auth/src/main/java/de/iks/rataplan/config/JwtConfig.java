package de.iks.rataplan.config;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import de.iks.rataplan.dto.OpenIDAutoDiscoveryDTO;
import de.iks.rataplan.service.MultiIssuerJWSKeySelector;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.gen.JWKGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ConfigurationProperties("token")
@Data
@Slf4j
public class JwtConfig {
    private long lifetime = 1200;
    private String issuer = "https://auth.drumdibum.de/issuer";
    
    @Bean
    public OpenIDAutoDiscoveryDTO autoDiscovery() {
        return OpenIDAutoDiscoveryDTO.builder().issuer(issuer).jwks_uri(issuer + "/jwks").build();
    }
    
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
    
    
    
    @Bean
    public JwtDecoder jwtDecoder(@Value("${trusted.issuers:}") String issuerPattern) {
        Predicate<String> trust = Pattern.compile(issuerPattern).asPredicate().or(issuer::equals);
        DefaultJWTProcessor<SecurityContext> processor = new DefaultJWTProcessor<>();
        processor.setJWTClaimsSetAwareJWSKeySelector(new MultiIssuerJWSKeySelector(trust));
        NimbusJwtDecoder backendDecoder = new NimbusJwtDecoder(processor);
        backendDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefault(),
            t -> trust.test(t.getIssuer().toString()) ?
                OAuth2TokenValidatorResult.success() :
                OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN))
        ));
        return backendDecoder;
    }
}