package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthToken;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AuthTokenService {
    AuthToken saveAuthTokenToUserWithMail(String mail);

    String generateAuthToken(int length);

    int getIdFromAuthToken(String token);

    int deleteById(int userId);

    boolean verifyAuthToken(String token);
}
