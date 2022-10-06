package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    String JWT_COOKIE_NAME = "jwttoken";
    ResponseEntity<AuthUser> getUserData(String token);
}
