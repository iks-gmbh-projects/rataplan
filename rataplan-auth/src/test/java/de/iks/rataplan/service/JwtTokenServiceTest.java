package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.dto.UserDTO;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {
    @Mock
    private io.jsonwebtoken.SigningKeyResolver signingKeyResolver;
    @Mock
    private CryptoService cryptoService;
    private JwtTokenService jwtTokenService;
    
    @BeforeEach
    void setup() throws NoSuchAlgorithmException {
        jwtTokenService = new JwtTokenServiceImpl(cryptoService, new JwtConfig(), signingKeyResolver);
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair idKey = gen.generateKeyPair();
        KeyPair signingKey = gen.generateKeyPair();
        lenient().when(cryptoService.idKey()).thenReturn(idKey.getPublic());
        lenient().when(cryptoService.idKeyP()).thenReturn(idKey.getPrivate());
        lenient().when(signingKeyResolver.resolveSigningKey(Mockito.any(), Mockito.any(Claims.class)))
            .thenReturn(signingKey.getPublic());
    }
    
    @Test
    void generateTokenAndValidateTokenAndGetUsernameFromToken() {
        UserDTO user = new UserDTO();
        
        user.setUsername("Peter");
        user.setMail("peter@sch.mitz");
        //		user.setPassword("geheim");
        
        String token = jwtTokenService.generateLoginToken(user);
        assertNotNull(token);
        
        assertTrue(jwtTokenService.isTokenValid(token));
        
        String username = jwtTokenService.getUsernameFromToken(token);
        assertEquals(username, "Peter");
    }
    
    @Test
    void generateAccountConfirmationAndRetrieveId() {
        UserDTO user = new UserDTO();
        user.setId(1);
        
        String token = jwtTokenService.generateAccountConfirmationToken(user);
        Integer userId = jwtTokenService.getUserIdFromAccountConfirmationToken(token);
        
        assertNotNull(token);
        assertTrue(jwtTokenService.isTokenValid(token));
        assertEquals(userId, user.getId());
    }
    
    @Test
    void generateResetPasswordTokenAndRetrieveEmail() {
        String email = "test@test.com";
        String jwt = this.jwtTokenService.generateResetPasswordToken(email);
        assertNotNull(jwt);
        assertTrue(jwtTokenService.isTokenValid(jwt));
        assertEquals(email, this.jwtTokenService.getEmailFromResetPasswordToken(jwt));
    }
}
