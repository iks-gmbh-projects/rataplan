package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.AuthUser;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<AuthUser> getUserData(String token);

    ResponseEntity<AuthToken> saveAuthTokenToUserWithMail(String mail);
}
