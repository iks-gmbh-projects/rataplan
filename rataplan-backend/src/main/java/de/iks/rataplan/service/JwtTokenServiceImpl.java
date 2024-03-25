package de.iks.rataplan.service;

import de.iks.rataplan.domain.JwtConfig;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService, Serializable {
    private final CryptoService cryptoService;
    public static final String CLAIM_PURPOSE = "purpose";
    public static final String PURPOSE_ID = "id";

    private final JwtConfig jwtConfig;


    @Override
    public String generateAuthBackendParticipantToken(Integer id) {
        String jwt = Jwts.builder().setClaims(generateIdClaims(String.valueOf(id)))
                .signWith(SignatureAlgorithm.RS256, this.cryptoService.getPrivateKey()).compact();
        return jwt;
    }

    @Override
    public Claims generateIdClaims(String id) {
        Claims claims = generateStandardClaims();
        claims.put(CLAIM_PURPOSE,PURPOSE_ID);
        claims.setSubject(id);
        return claims;
    }

    @Override
    public Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtConfig.getLifetime());
    }

    @Override
    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(cryptoService.getPrivateKey()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid JWT");
        }
        return claims;
    }

    @Override
    public Claims generateStandardClaims() {
        Claims claims = Jwts.claims();
        claims.setIssuedAt(new Date());
        claims.setIssuer(jwtConfig.getIssuer());
        claims.setExpiration(generateExpirationDate());
        return claims;
    }

}
