package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;

import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {
    String CLAIM_USERID = "user_id";
    AuthUser getUserData(Jwt token);
}