package de.iks.rataplan.service;

import io.jsonwebtoken.Claims;

public interface JwtTokenService {

    String generateAuthBackendParticipantToken(Integer id);
    String generateIDToken();
    Claims getClaimsFromToken(String token);
}