package de.iks.rataplan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class BackendMessageServiceImpl implements BackendMessageService {
    private final RestTemplate template;
    private final JwtTokenService jwtTokenService;
    
    @Value("${backend.appointment.urltemplate.delete}")
    private String deleteUrlTemplate;
    @Value("${backend.appointment.urltemplate.anonymize}")
    private String anonymizeUrlTemplate;
    
    @Override
    public ResponseEntity<?> deleteUserData(long userId) {
        RequestEntity<String> requestEntity = RequestEntity.method(
            HttpMethod.DELETE,
            URI.create(String.format(deleteUrlTemplate, userId))
        ).body(jwtTokenService.generateIdToken());
        return template.exchange(requestEntity, String.class);
    }
    
    @Override
    public ResponseEntity<?> anonymizeUserData(long userId) {
        RequestEntity<String> requestEntity = RequestEntity.method(
            HttpMethod.POST,
            URI.create(String.format(anonymizeUrlTemplate, userId))
        ).body(jwtTokenService.generateIdToken());
        return template.exchange(requestEntity, String.class);
    }
}
