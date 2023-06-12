package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class AuthServiceImpl implements AuthService {

    private static final String JWT_COOKIE_NAME = "jwttoken";

    @Value("${rataplan.auth.url}")
    private String authUrl;

    @Autowired
    private RestTemplate restTemplate; // moving this to constructor injection breaks the mocks in AuthServiceTest somehow

    public ResponseEntity<AuthUser> getUserData(String token) {
        String url = authUrl + "/users/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_COOKIE_NAME, token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, AuthUser.class);
    }

    public ResponseEntity<AuthToken> saveAuthTokenToUserWithMail(String mail) {
        String url = authUrl + "/users/saveAuthToken";

        return restTemplate.postForEntity(url, mail, AuthToken.class);
    }
}
