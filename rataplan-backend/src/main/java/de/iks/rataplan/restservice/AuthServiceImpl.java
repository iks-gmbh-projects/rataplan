package de.iks.rataplan.restservice;

import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.function.Predicate;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    public static final String CLAIM_PURPOSE = "purpose";
    public static final String CLAIM_USERID = "user_id";
    public static final String PURPOSE_LOGIN = "login";
    public static final String PURPOSE_ID = "id";

    private final KeyExchangeConfig keyExchangeConfig;
    private final SigningKeyResolver keyResolver;
    private final RestTemplate restTemplate;
    
    private Claims parseToken(String token) {
        try{
            return Jwts.parser()
                .setSigningKeyResolver(keyResolver)
                .parseClaimsJws(token)
                .getBody();
        } catch(SignatureException ex) {
            if(keyExchangeConfig.isShortenedCache()) throw ex;
            return Jwts.parser()
                .parseClaimsJws(token)
                .getBody();
        }
    }

    public AuthUser getUserData(String token) {
        try {
            Claims claims = parseToken(token);
            if (!PURPOSE_LOGIN.equals(claims.get(CLAIM_PURPOSE))) throw new InvalidTokenException("Invalid Token");
            if (keyExchangeConfig.getValidIssuer()
                .stream()
                .noneMatch(Predicate.isEqual(claims.getIssuer()))
            ) throw new InvalidTokenException("Invalid Token");
            return new AuthUser(
                claims.get(CLAIM_USERID, Integer.class),
                claims.getSubject()
            );
        } catch(ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid Token");
        }
    }
    
    @Override
    public String fetchDisplayName(Integer userId) {
        return restTemplate.getForEntity(keyExchangeConfig.getDisplayNameURL()+userId, String.class).getBody();
    }
    
    public boolean isValidIDToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (!PURPOSE_ID.equals(claims.get(CLAIM_PURPOSE))) return false;
            return keyExchangeConfig.getValidIssuer()
                .stream()
                .anyMatch(Predicate.isEqual(claims.getIssuer()));
        } catch(ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
