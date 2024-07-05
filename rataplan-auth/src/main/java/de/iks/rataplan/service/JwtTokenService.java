package de.iks.rataplan.service;

import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.UserDTO;

import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtTokenService {
    String CLAIM_SCOPE = "scope";
    String SCOPE_LOGIN = "login";
    String SCOPE_ID = "id";
    String SCOPE_ACCOUNT_CONFIRMATION = "account-confirmation";
    String SCOPE_RESET_PASSWORD = "reset-password";
    String CLAIM_USERID = "user_id";
    String CLAIM_VERSION = "version";
    String CLAIM_MAIL = "mail";
    String SCOPE_UPDATE_EMAIL = "update-email";
    
    Integer getUserId(Jwt jwt);
    
    String generateLoginToken(RataplanUserDetails user);
    
    String generateIdToken();
    
    String generateAccountConfirmationToken(UserDTO userDTO);
    
    String generateConfirmEmailUpdateToken(UserDTO userDTO, User user);
    
    String generateResetPasswordToken(String email);
}