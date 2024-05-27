package iks.surveytool.services;

import iks.surveytool.domain.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthUser getUserData(Jwt token) {
        return new AuthUser(token.<Number>getClaim(CLAIM_USERID).longValue(), token.getSubject());
    }
}