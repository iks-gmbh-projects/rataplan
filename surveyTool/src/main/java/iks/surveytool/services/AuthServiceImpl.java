package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {
    private final String authUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthServiceImpl(
        @Value("${rataplan.auth.url}") String authUrl,
        RestTemplate restTemplate
    ) {
        this.authUrl = authUrl;
        this.restTemplate = restTemplate;
    }

    public AuthUser getUserData(String token) {
        String url = authUrl + "/users/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_COOKIE_NAME, token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        try {
            final ResponseEntity<AuthUser> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AuthUser.class
            );
            if (response.getStatusCode().is2xxSuccessful()) return response.getBody();
            else return null;
        } catch (RestClientResponseException ex) {
            log.catching(Level.INFO, ex);
            return null;
        }
    }

    @Override
    public boolean validateBackendSecret(String secret) {
        return true; // TODO verify that the secret string comes from the auth backend
    }
}
