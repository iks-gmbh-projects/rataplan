package de.iks.rataplan.service;

import de.iks.rataplan.dto.UserDTO;

import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtTokenService {
    String CLAIM_SCOPE = "scope";
    String SCOPE_LOGIN = "login";
    String SCOPE_ID = "id";
    String SCOPE_ACCOUNT_CONFIRMATION = "account-confirmation";
    String SCOPE_RESET_PASSWORD = "reset-password";
    
    Integer getUserId(Jwt jwt);
    
    String generateLoginToken(RataplanUserDetails user);
    
    String generateIdToken();
    
    String generateAccountConfirmationToken(UserDTO userDTO);
    
    String generateResetPasswordToken(String email);
}