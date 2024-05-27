package iks.surveytool.services;

import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.SurveyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface SurveyResponseService {
    ResponseEntity<SurveyResponseDTO> processSurveyResponseDTOs(SurveyResponseDTO surveyResponseDTO);

    boolean validateUniqueParticipation(SurveyResponse surveyResponse);

    ResponseEntity<List<SurveyResponseDTO>> processSurveyResponseDTOs(String accessId, Jwt authToken);
    ResponseEntity<?> deleteSurveyResponsesByUserId(long id);
    ResponseEntity<?> anonymizeSurveyResponsesByUserId(long id);
}