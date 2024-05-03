package de.iks.rataplan.restservice;

import de.iks.rataplan.config.KeyExchangeConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.restservice.EmailNotificationDTO;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.dto.restservice.UserNotificationDTO;
import de.iks.rataplan.exceptions.InvalidTokenException;
import de.iks.rataplan.service.JwtTokenService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    public static final String CLAIM_PURPOSE = "purpose";
    public static final String CLAIM_USERID = "user_id";
    public static final String PURPOSE_LOGIN = "login";
    public static final String PURPOSE_ID = "id";
    
    private final KeyExchangeConfig keyExchangeConfig;
    private final JwtTokenService jwtTokenService;
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
        } catch(RestClientException ignored) {}
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
    
    @Override
    public void sendUserNotifications(
        Collection<Integer> recipients,
        NotificationType type,
        String subject,
        String content,
        String summaryContent
    ) {
        if(recipients.isEmpty()) return;
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt", jwtTokenService.generateIDToken());
        HttpEntity<List<UserNotificationDTO>> request = new HttpEntity<>(recipients.stream()
            .map(recipient -> new UserNotificationDTO(
                recipient,
                type.name,
                subject,
                content,
                summaryContent
            ))
            .collect(Collectors.toUnmodifiableList()),
            headers
        );
        restTemplate.postForObject(
            keyExchangeConfig.getNotificationURL(),
            request,
            Boolean.class
        );
    }
    
    @Override
    public void sendMailNotifications(
        Collection<String> recipients,
        NotificationType type,
        String subject,
        String content,
        String summaryContent
    ) {
        if(recipients.isEmpty()) return;
        HttpHeaders headers = new HttpHeaders();
        headers.add("jwt", jwtTokenService.generateIDToken());
        HttpEntity<List<EmailNotificationDTO>> request = new HttpEntity<>(recipients.stream()
            .map(recipient -> new EmailNotificationDTO(
                recipient,
                type.name,
                subject,
                content,
                summaryContent
            ))
            .collect(Collectors.toUnmodifiableList()),
            headers
        );
        restTemplate.postForObject(
            keyExchangeConfig.getNotificationURL(),
            request,
            Boolean.class
        );
    }
}