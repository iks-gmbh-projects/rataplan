package de.iks.rataplan.service;

import de.iks.rataplan.config.BackendMessageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackendMessageServiceImpl implements BackendMessageService {
    private final BackendMessageConfig backendMessageConfig;
    private final RestTemplate template;
    private final JwtTokenService jwtTokenService;
    
    @Override
    public ResponseEntity<?> deleteUserData(long userId) {
        RequestEntity<String> requestEntity = RequestEntity.method(
            HttpMethod.DELETE,
            UriComponentsBuilder.fromHttpUrl(backendMessageConfig.getDelete())
                .buildAndExpand(userId)
                .toUri()
        ).body(jwtTokenService.generateIdToken());
        return template.exchange(requestEntity, String.class);
    }
    
    @Override
    public ResponseEntity<?> anonymizeUserData(long userId) {
        return template.postForEntity(
            backendMessageConfig.getAnonymize(),
            jwtTokenService.generateIdToken(),
            String.class,
            userId
        );
    }
}
