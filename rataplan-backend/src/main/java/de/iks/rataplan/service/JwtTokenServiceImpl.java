package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtEncoder jwtEncoder;
    public static final String CLAIM_SCOPE = "scope";
    public static final String SCOPE_ID = "id";

    private final JwtConfig jwtConfig;
    
    private Jwt generateToken(JwtClaimsSet.Builder claims) {
        Instant now = Instant.now();
        claims.issuedAt(now);
        claims.expiresAt(now.plusSeconds(jwtConfig.getLifetime()));
        claims.issuer(jwtConfig.getIssuer());
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS512).build(), claims.build()));
    }
    
    @Override
    public Jwt generateIDToken() {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(jwtConfig.getIssuer());
        claims.claim(CLAIM_SCOPE, SCOPE_ID);
        return generateToken(claims);
    }
}