package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthUser getUserData(String token) throws InvalidTokenException;
    String fetchDisplayName(Integer userId);
}
