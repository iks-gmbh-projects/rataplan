package de.iks.rataplan.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@TestExecutionListeners(
    value = TransactionDbUnitTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
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
        Assertions.assertEquals("1", claims.getSubject());
    }

    @Test
    public void assertParsingFailsWrongSigningKey() throws NoSuchAlgorithmException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        String jwts = jwtTokenService.generateAuthBackendParticipantToken(1);
        Assertions.assertThrows(SignatureException.class,() -> Jwts.parser().setSigningKey(keyPair.getPublic()).parse(jwts));
    }
}
