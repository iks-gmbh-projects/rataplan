package iks.surveytool.restservice;

import iks.surveytool.domain.AuthUser;
import iks.surveytool.services.AuthService;
import iks.surveytool.services.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthServiceTest {
    private AuthServiceImpl authService;
    
    private static final AuthUser AUTHUSER_1 = new AuthUser(1L, "iks");
    
    @BeforeEach
    public void setup() {
        authService = new AuthServiceImpl();
    }
    
    @Test
    public void getUserData() {
        Jwt token = Jwt.withTokenValue("bla")
            .subject(AUTHUSER_1.getUsername())
            .claim(AuthService.CLAIM_USERID, AUTHUSER_1.getId())
            .header("kid", "bla")
            .build();
        
        assertEquals(AUTHUSER_1, authService.getUserData(token));
    }
}