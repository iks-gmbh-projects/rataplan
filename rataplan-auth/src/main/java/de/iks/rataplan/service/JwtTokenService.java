package de.iks.rataplan.service;

import de.iks.rataplan.dto.UserDTO;
import io.jsonwebtoken.Claims;

import java.util.Date;

public interface JwtTokenService {
    public Claims getAccountConfirmationClaims(String token);

    public String getUsernameFromToken(String token);

    public String generateLoginToken(UserDTO user);

    public Date getTokenExpiration(String token);

    public boolean isTokenValid(String token);

    public String generateIdToken();

    public String generateAccountConfirmationToken(UserDTO userDTO);
}
