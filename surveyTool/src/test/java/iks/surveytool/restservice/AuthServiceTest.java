package iks.surveytool.restservice;

import iks.surveytool.config.KeyExchangeConfig;
import iks.surveytool.domain.AuthUser;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.AuthServiceImpl;
import iks.surveytool.services.InvalidTokenException;
import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {KeyExchangeConfig.class, AuthServiceImpl.class})
public class AuthServiceTest {
    @Autowired
    private KeyExchangeConfig keyExchangeConfig;
    
    @MockBean
    private SigningKeyResolver keyResolver;
    
    @Autowired
    private AuthService authService;
    
    private static final AuthUser AUTHUSER_1 = new AuthUser(1L, "iks");
    
    private KeyPair keyPair;
    
    @BeforeEach
    public void initKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
        when(keyResolver.resolveSigningKey(any(JwsHeader.class), any(Claims.class)))
            .thenReturn(keyPair.getPublic());
    }
    
    @Test
    public void getUserData() {
        Claims claims = Jwts.claims();
        claims.setIssuer("test");
        claims.setSubject(AUTHUSER_1.getUsername());
        claims.put(AuthServiceImpl.CLAIM_USERID, AUTHUSER_1.getId());
        claims.put(AuthServiceImpl.CLAIM_PURPOSE, AuthServiceImpl.PURPOSE_LOGIN);
        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        keyExchangeConfig.setValidIssuer("test");
        
        String token = Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.RS512, keyPair.getPrivate())
            .compact();
        
        assertEquals(AUTHUSER_1, authService.getUserData(token));
    }
    
    @Test
    public void getUserDataShouldFailInvalidToken() {
        assertThrows(
            InvalidTokenException.class,
            () -> authService.getUserData("blashgiodrfngslkshdr")
        );
    }
    
    @Test
    public void getUserDataShouldFailUnsignedToken() {
        Claims claims = Jwts.claims();
        claims.setIssuer("test");
        claims.setSubject(AUTHUSER_1.getUsername());
        claims.put(AuthServiceImpl.CLAIM_USERID, AUTHUSER_1.getId());
        claims.put(AuthServiceImpl.CLAIM_PURPOSE, AuthServiceImpl.PURPOSE_LOGIN);
        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        keyExchangeConfig.setValidIssuer("test");
        
        String token = Jwts.builder().setClaims(claims).compact();
        assertThrows(
            InvalidTokenException.class,
            () -> authService.getUserData(token)
        );
    }
}
