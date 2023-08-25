package de.iks.rataplan.restservice;

import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.config.TestConfig;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_1;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext
public class AuthServiceTest {
    @Autowired
    private KeyExchangeConfig keyExchangeConfig;
    
    @Autowired
    private SigningKeyResolver keyResolver;
    
    @Autowired
    private AuthService authService;
    
    private KeyPair keyPair;
    
    @Before
    public void initKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
        when(keyResolver.resolveSigningKey(Matchers.isA(JwsHeader.class), Matchers.isA(Claims.class))).thenReturn(
            keyPair.getPublic());
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
    
    @Test(expected = InvalidTokenException.class)
    public void getUserDataShouldFailInvalidToken() {
        authService.getUserData("blashgiodrfngslkshdr");
    }
    
    @Test(expected = InvalidTokenException.class)
    public void getUserDataShouldFailUnsignedToken() {
        Claims claims = Jwts.claims();
        claims.setIssuer("test");
        claims.setSubject(AUTHUSER_1.getUsername());
        claims.put(AuthServiceImpl.CLAIM_USERID, AUTHUSER_1.getId());
        claims.put(AuthServiceImpl.CLAIM_PURPOSE, AuthServiceImpl.PURPOSE_LOGIN);
        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        keyExchangeConfig.setValidIssuer("test");
        
        String token = Jwts.builder().setClaims(claims).compact();
        
        authService.getUserData(token);
    }
}
