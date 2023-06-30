package de.iks.rataplan.service;

import de.iks.rataplan.config.SurveyToolMessageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class SurveyToolMessageServiceImpl implements SurveyToolMessageService {
    private final SurveyToolMessageConfig surveyToolMessageConfig;
    private final RestTemplate template;
    private final JwtTokenService jwtTokenService;
    @Override
    public ResponseEntity<?> deleteUserData(long userId) {
        RequestEntity<String> requestEntity = RequestEntity.method(
            HttpMethod.DELETE,
            UriComponentsBuilder.fromHttpUrl(surveyToolMessageConfig.getDelete())
                .buildAndExpand(userId)
                .toUri()
        ).body(jwtTokenService.generateIdToken());
        return template.exchange(requestEntity, String.class);
    }

    @Override
    public ResponseEntity<?> anonymizeUserData(long userId) {
        return template.postForEntity(surveyToolMessageConfig.getAnonymize(), jwtTokenService.generateIdToken(), String.class, userId);
    }
}
