package de.iks.rataplan.service;

import de.iks.rataplan.dto.UserDTO;

import java.util.Date;

public interface JwtTokenService {
    Integer getUserIdFromAccountConfirmationToken(String token);
    
    String getUsernameFromToken(String token);
    Integer getUserIdFromToken(String token);
    
    String generateLoginToken(UserDTO user);
    
    Date getTokenExpiration(String token);
    
    boolean isTokenValid(String token);
    
    String generateIdToken();
    
    String generateAccountConfirmationToken(UserDTO userDTO);
    
    String generateResetPasswordToken(String email);
    
    String getEmailFromResetPasswordToken(String token);
    
    boolean isBackendTokenValid(String jwt);
    int getUserIdFromBackendToken(String jwt);
}
