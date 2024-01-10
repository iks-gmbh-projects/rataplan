package de.iks.rataplan.restservice;

import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {
    @Autowired
    private KeyExchangeConfig keyExchangeConfig;
    
    @MockBean
    private SigningKeyResolver keyResolver;
    
    @Autowired
    private AuthService authService;
    
    private KeyPair keyPair;
    
    @BeforeEach
    public void initKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
        when(keyResolver.resolveSigningKey(
            isA(JwsHeader.class),
            isA(Claims.class)
        ))
            .thenReturn(keyPair.getPublic());
    }
    
    @Test
    public void getUserData() {
        Claims claims = Jwts.claims();
        claims.setIssuer("test");
        claims.setSubject(AUTHUSER_1.getUsername());
        claims.put(AuthServiceImpl.CLAIM_USERID, AUTHUSER_1.getId());
        claims.put(AuthServiceImpl.CLAIM_PURPOSE, AuthServiceImpl.PURPOSE_LOGIN);
        claims.setExpiration(new Date(System.currentTimeMillis() + 2000));
        keyExchangeConfig.setValidIssuer("test");
        
        String token = Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS512, keyPair.getPrivate())
            .compact();
        
        assertEquals(AUTHUSER_1, authService.getUserData(token));
    }
    
    @Test
    public void getUserDataShouldFailInvalidToken() {Assertions.assertThrows(InvalidTokenException.class,() -> authService.getUserData("blashgiodrfngslkshdr"));}
    
    @Test()
    public void getUserDataShouldFailUnsignedToken() {
        Claims claims = Jwts.claims();
        claims.setIssuer("test");
        claims.setSubject(AUTHUSER_1.getUsername());
        claims.put(AuthServiceImpl.CLAIM_USERID, AUTHUSER_1.getId());
        claims.put(AuthServiceImpl.CLAIM_PURPOSE, AuthServiceImpl.PURPOSE_LOGIN);
        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        keyExchangeConfig.setValidIssuer("test");
        
        String token = Jwts.builder().setClaims(claims).compact();
        assertThrows(InvalidTokenException.class, () -> authService.getUserData(token));
    }
}
