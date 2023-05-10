package de.iks.rataplan.service;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.exceptions.InvalidUserDataException;
import de.iks.rataplan.repository.AuthTokenRepository;
import de.iks.rataplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    private final UserService userService;

    private final AuthTokenRepository authTokenRepository;

    private final CryptoServiceImpl cryptoService;

    @Value("${token.lifetime}")
    private int tokenLifetime;

    @Override
    public AuthToken saveAuthTokenToUserWithMail(String mail) {
        mail = mail.trim();
        if(mail.isEmpty()) throw new InvalidUserDataException();
        User user = userService.getUserFromEmail(mail);
        if (user == null) throw new NullPointerException();
        int id = user.getId();
        String token = generateAuthToken(6);
        while (authTokenRepository.findByToken(token) != null) {
            token = generateAuthToken(6);
        }
        authTokenRepository.deleteById(id);
        AuthToken authToken = new AuthToken(id, token);
        return authTokenRepository.save(authToken);
    }

    @Override
    public boolean verifyAuthToken(String token) {
        AuthToken authToken = authTokenRepository.findByToken(token.trim());
        if(authToken == null) return false;
        Date currentDate = new java.util.Date();
        currentDate.setTime(currentDate.getTime() - tokenLifetime);
        return (authToken.getCreatedDateTime().getTime() - currentDate.getTime() > 0);
    }

    @Override
    public String generateAuthToken(int length) {
        int leftLimit = 48; // number 0
        int rightLimit = 122; // letter z

        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Override
    public int getIdFromAuthToken(String token) {
        return authTokenRepository.findByToken(token.trim()).getId();
    }

    @Override
    public int deleteById(int userId) {
        return authTokenRepository.deleteById(userId);
    }

}
