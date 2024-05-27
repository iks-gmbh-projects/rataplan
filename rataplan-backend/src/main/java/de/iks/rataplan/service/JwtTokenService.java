package de.iks.rataplan.service;

import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtTokenService {
    Jwt generateIDToken();
}