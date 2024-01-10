package iks.surveytool.services;

import iks.surveytool.config.KeyExchangeConfig;
import iks.surveytool.domain.AuthUser;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {
    public static final String CLAIM_PURPOSE = "purpose";
    public static final String CLAIM_USERID = "user_id";
    public static final String PURPOSE_LOGIN = "login";
    public static final String PURPOSE_ID = "id";
    
    private final KeyExchangeConfig keyExchangeConfig;
    private final SigningKeyResolver keyResolver;
    
    private Claims parseToken(String token, String purpose) {
        return Jwts.parser()
            .setSigningKeyResolver(keyResolver)
            .requireIssuer(keyExchangeConfig.getValidIssuer())
            .require(CLAIM_PURPOSE, purpose)
            .parseClaimsJws(token)
            .getBody();
    }
    
    public AuthUser getUserData(String token) {
        try {
            Claims claims = parseToken(token, PURPOSE_LOGIN);
            return new AuthUser(
                claims.get(CLAIM_USERID, Integer.class).longValue(),
                claims.getSubject()
            );
        } catch (ExpiredJwtException |
                 SignatureException |
                 MalformedJwtException |
                 UnsupportedJwtException |
                 IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid Token");
        }
    }

    @Override
    public boolean validateBackendSecret(String token) {
        try {
            Claims claims = parseToken(token, PURPOSE_ID);
            return Objects.equals(claims.getSubject(), claims.getIssuer());
        } catch (ExpiredJwtException |
                 SignatureException |
                 MalformedJwtException |
                 UnsupportedJwtException |
                 IllegalArgumentException ex) {
            return false;
        }
    }
}
