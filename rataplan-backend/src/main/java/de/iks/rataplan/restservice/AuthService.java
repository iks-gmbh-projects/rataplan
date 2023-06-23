package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;

public interface AuthService {
    AuthUser getUserData(String token) throws InvalidTokenException;
    String fetchDisplayName(Integer userId);
    public boolean isValidIDToken(String token);
}
