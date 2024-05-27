package de.iks.rataplan.restservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static de.iks.rataplan.testutils.TestConstants.AUTHUSER_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthServiceTest {
    private AuthService authService;
    
    @BeforeEach
    public void setup() {
        authService = new AuthServiceImpl(null, null, null);
    }
    
    @Test
    public void getUserData() {
        Jwt token = Jwt.withTokenValue("bla")
            .subject(AUTHUSER_1.getUsername())
            .claim(AuthService.CLAIM_USERID, AUTHUSER_1.getId())
            .claim("scope", "id")
            .header("kid", "blabla")
            .build();
        
        assertEquals(AUTHUSER_1, authService.getUserData(token));
    }
}