package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.repository.AuthTokenRepository;
import de.iks.rataplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    private final UserRepository userRepository;

    private final AuthTokenRepository authTokenRepository;

    @Value("${token.lifetime}")
    private int tokenLifetime;

    @Override
    public AuthToken saveAuthTokenToUserWithMail(String mail) {
        int id = userRepository.findOneByMail(mail).getId();
        //length 4 creates a token with 6 symbols and i dont know why
        String token = generateAuthToken(4);
        while (authTokenRepository.findByToken(token) != null) {
            token = generateAuthToken(4);
        }
        authTokenRepository.deleteById(id);
        AuthToken authToken = new AuthToken(id, token);
        return authTokenRepository.save(authToken);
    }

    @Override
    public boolean verifyAuthToken(String token) {
        AuthToken authToken = authTokenRepository.findByToken(token);
        if(authToken == null) return false;
        Date currentDate = new java.util.Date();
        currentDate.setTime(currentDate.getTime() - tokenLifetime);
        return (authToken.getCreatedDateTime().getTime() - currentDate.getTime() > 0);
    }

    @Override
    public String generateAuthToken(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[length];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    @Override
    public int getIdFromAuthToken(String token) {
        return authTokenRepository.findByToken(token).getId();
    }

    @Override
    public int deleteById(int userId) {
        return authTokenRepository.deleteById(userId);
    }

}
