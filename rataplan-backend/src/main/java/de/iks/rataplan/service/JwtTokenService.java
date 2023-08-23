package de.iks.rataplan.service;

import io.jsonwebtoken.Claims;

import java.util.Date;

public interface JwtTokenService {

    String generateAuthBackendParticipantToken(Integer id);

    Claims generateIdClaims(String id);

    Date generateExpirationDate();

    Claims getClaimsFromToken(String token);

    Claims generateStandardClaims();
}