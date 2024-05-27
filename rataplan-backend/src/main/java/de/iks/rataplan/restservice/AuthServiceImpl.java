package de.iks.rataplan.restservice;

import de.iks.rataplan.config.AuthBackendUrlConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.dto.restservice.EmailNotificationDTO;
import de.iks.rataplan.dto.restservice.NotificationType;
import de.iks.rataplan.dto.restservice.UserNotificationDTO;
import de.iks.rataplan.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    
    
    private final AuthBackendUrlConfig authBackendUrlConfig;
    private final JwtTokenService jwtTokenService;
    private final RestTemplate restTemplate;
    
    @Override
    public AuthUser getUserData(Jwt token) {
        return new AuthUser(
            token.<Number>getClaim(CLAIM_USERID).intValue(),
            token.getSubject()
        );
    }
    @Override
    public Integer fetchUserIdFromEmail(String email) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(authBackendUrlConfig.getEmail())
                .queryParam("email", email)
                .toUriString();
            ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) return response.getBody();
        } catch(RestClientException ignored) {}
        return null;
    }
    
    @Override
    public String fetchDisplayName(Integer userId) {
        String url = UriComponentsBuilder.fromHttpUrl(authBackendUrlConfig.getDisplayName())
            .pathSegment(userId.toString())
            .toUriString();
        return restTemplate.getForObject(url, String.class);
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
        headers.setBearerAuth(jwtTokenService.generateIDToken().getTokenValue());
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
            authBackendUrlConfig.getNotification(),
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
        headers.setBearerAuth(jwtTokenService.generateIDToken().getTokenValue());
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
            authBackendUrlConfig.getNotification(),
            request,
            Boolean.class
        );
    }
}