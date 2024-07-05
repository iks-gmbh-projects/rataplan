package de.iks.rataplan.service;

import de.iks.rataplan.config.JwtConfig;
import de.iks.rataplan.domain.User;
import de.iks.rataplan.dto.UserDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtConfig jwtConfig;
    private final JwtEncoder encoder;
    
    @Override
    public Integer getUserId(Jwt jwt) {
        return Optional.ofNullable(jwt.<Number>getClaim(CLAIM_USERID)).map(Number::intValue).orElse(null);
    }
    @Override
    public String generateResetPasswordToken(String email) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(email);
        claims.claim(CLAIM_SCOPE, SCOPE_RESET_PASSWORD);
        return generateToken(claims, 24*3600*1000);
    }
    
    @Override
    public String generateLoginToken(RataplanUserDetails user) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(user.getUsername());
        claims.claim(CLAIM_USERID, user.getId());
        claims.claim(CLAIM_SCOPE, SCOPE_LOGIN);
        return generateToken(claims, jwtConfig.getLifetime());
    }
    
    @Override
    public String generateAccountConfirmationToken(UserDTO userDTO) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(userDTO.getId().toString());
        claims.claim(CLAIM_SCOPE, SCOPE_ACCOUNT_CONFIRMATION);
        return generateToken(claims, 20*60);
    }
    
    @Override
    public String generateConfirmEmailUpdateToken(UserDTO userDTO, User user) {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(userDTO.getId().toString());
        claims.claim(CLAIM_VERSION, user.getVersion());
        claims.claim(CLAIM_MAIL, userDTO.getMail());
        claims.claim(CLAIM_SCOPE, SCOPE_UPDATE_EMAIL);
        return generateToken(claims, jwtConfig.getLifetime());
    }
    
    private String generateToken(JwtClaimsSet.Builder claims, long lifetime) {
        Instant n = Instant.now();
        JwtClaimsSet claimsSet = claims.issuer(jwtConfig.getIssuer())
            .issuedAt(n)
            .expiresAt(n.plusSeconds(lifetime))
            .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS512).build(), claimsSet)).getTokenValue();
    }
    
    @Override
    public String generateIdToken() {
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder();
        claims.subject(jwtConfig.getIssuer());
        claims.claim(CLAIM_SCOPE, SCOPE_ID);
        return generateToken(claims, 60);
    }
}