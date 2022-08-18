package de.iks.rataplan.restservice;

import de.iks.rataplan.domain.AuthToken;
import de.iks.rataplan.domain.ResetPasswordData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.PasswordChange;
import de.iks.rataplan.exceptions.MalformedException;


@Service
public class AuthServiceImpl implements AuthService {

    private static final String JWT_COOKIE_NAME = "jwttoken";

    @Value("${rataplan.auth.url}")
    private String authUrl;

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<AuthUser> getUserData(String token) {
        String url = authUrl + "/users/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_COOKIE_NAME, token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, AuthUser.class);
    }

    public ResponseEntity<AuthUser> registerUser(AuthUser authUser) {
        String url = authUrl + "/users/register";
        return restTemplate.postForEntity(url, authUser, AuthUser.class);
    }

    public ResponseEntity<Boolean> checkIfMailExists(String mail) {
        String url = authUrl + "/users/mailExists";

        return restTemplate.postForEntity(url, mail, Boolean.class);
    }

    public ResponseEntity<Boolean> checkIfUsernameExists(String username) {
        String url = authUrl + "/users/usernameExists";

        return restTemplate.postForEntity(url, username, Boolean.class);
    }

    public ResponseEntity<AuthToken> saveAuthTokenToUserWithMail(String mail) {
        String url = authUrl + "/users/saveAuthToken";

        return restTemplate.postForEntity(url, mail, AuthToken.class);
    }

    public ResponseEntity<AuthUser> loginUser(AuthUser authUser) {
        String url = authUrl + "/users/login";
        return restTemplate.postForEntity(url, authUser, AuthUser.class);
    }

    @Override
    public ResponseEntity<Boolean> changePassword(String token, PasswordChange passwords) {
        String url = authUrl + "/users/profile/changePassword";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JWT_COOKIE_NAME, token);
        HttpEntity<PasswordChange> entity = new HttpEntity<>(passwords, headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, entity, Boolean.class);
        if (response.getBody() == false) {
            throw new MalformedException(
                    "Password has not been changed.");
        }

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Boolean> resetPassword(ResetPasswordData resetPasswordData) {
        String url = authUrl + "/users/resetPassword";

        HttpEntity<ResetPasswordData> entity = new HttpEntity<>(resetPasswordData);

        ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.POST, entity, Boolean.class);

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

}
