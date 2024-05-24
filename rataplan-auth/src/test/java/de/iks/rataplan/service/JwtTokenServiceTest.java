package de.iks.rataplan.service;

import de.iks.rataplan.config.IDKeyConfig;
import de.iks.rataplan.config.JwtConfig;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import de.iks.rataplan.dto.UserDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {
    private JwtTokenService jwtTokenService;
    private JwtDecoder jwtDecoder;
    
    @BeforeEach
    void setup() {
        IDKeyConfig idKeyConfig = new IDKeyConfig();
        JwtConfig jwtConfig = new JwtConfig();
        JWKSet jwkSet = jwtConfig.jwkSet(List.of(idKeyConfig.generatedKey()));
        jwtTokenService = new JwtTokenServiceImpl(jwtConfig, jwtConfig.jwtEncoder(jwkSet));
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(new JWSAlgorithmFamilyJWSKeySelector<>(
            JWSAlgorithm.Family.RSA,
            new ImmutableJWKSet<>(jwkSet)
        ));
        jwtDecoder = new NimbusJwtDecoder(jwtProcessor);
    }
    
    @Test
    void generateTokenAndValidateTokenAndGetUsernameFromToken() {
        RataplanUserDetails user = new RataplanUserDetails(1, "Peter", "peter@sch.mitz", null, true);
        
        String token = jwtTokenService.generateLoginToken(user);
        assertNotNull(token);
        
        assertEquals("Peter", jwtDecoder.decode(token).getSubject());
    }
    
    @Test
    void generateAccountConfirmationAndRetrieveId() {
        UserDTO user = new UserDTO();
        user.setId(1);
        
        String token = jwtTokenService.generateAccountConfirmationToken(user);
        assertNotNull(token);
        
        int userId = Integer.parseInt(jwtDecoder.decode(token).getSubject());
        assertEquals(user.getId(), userId);
    }
    @Test
    void generateResetPasswordTokenAndRetrieveEmail() {
        String email = "test@test.com";
        String jwt = this.jwtTokenService.generateResetPasswordToken(email);
        assertNotNull(jwt);
        
        
        assertEquals(email, jwtDecoder.decode(jwt).getSubject());
    }
}