package de.iks.rataplan.restservice;

import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.exceptions.InvalidTokenException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    public static final String CLAIM_PURPOSE = "purpose";
    public static final String CLAIM_USERID = "user_id";
    public static final String PURPOSE_LOGIN = "login";
    public static final String PURPOSE_ID = "id";
    
    private final KeyExchangeConfig keyExchangeConfig;
    private final SigningKeyResolver keyResolver;
    private final RestTemplate restTemplate;
    
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
                claims.get(CLAIM_USERID, Integer.class),
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
    public Integer fetchUserIdFromEmail(String email) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(keyExchangeConfig.getEmailURL())
                .queryParam("email", email)
                .toUriString();
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) return response.getBody();
        } catch(RestClientException ex) {
            log.error("rest error", ex);
        }
        return null;
    }
    
    @Override
    public String fetchDisplayName(Integer userId) {
        String url = UriComponentsBuilder.fromHttpUrl(keyExchangeConfig.getDisplayNameURL())
            .pathSegment(userId.toString())
            .toUriString();
        return restTemplate.getForObject(url, String.class);
    }
    
    public boolean isValidIDToken(String token) {
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
