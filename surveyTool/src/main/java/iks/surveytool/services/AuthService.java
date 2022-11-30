package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;

public interface AuthService {
    String JWT_COOKIE_NAME = "jwttoken";
    AuthUser getUserData(String token);
    boolean validateBackendSecret(String secret);
}
