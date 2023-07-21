package de.iks.rataplan.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import de.iks.rataplan.config.AppConfig;
import de.iks.rataplan.config.TestConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.security.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@Transactional
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class JwtServiceTest {


    @Autowired
    JwtTokenService jwtTokenService;
    @Autowired
    CryptoService cryptoService;

    @Test
    public void generateAuthBackendIdToken() {
        String jwts = jwtTokenService.generateAuthBackendParticipantToken(1);
        Claims claims = (Claims) Jwts.
                parser().setSigningKey(cryptoService.getPublicKey()).parse(jwts).getBody();
        Assert.assertEquals("1", claims.getSubject());
    }

    @Test(expected = SignatureException.class)
    public void assertParsingFailsWrongSigningKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        String jwts = jwtTokenService.generateAuthBackendParticipantToken(1);
        Jwts.parser().setSigningKey(keyPair.getPublic()).parse(jwts);
    }

    @Test
    public void expirationDateValid(){
        Claims claims = jwtTokenService.generateStandardClaims();
        Assert.assertEquals(60000, claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
    }




}
