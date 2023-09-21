package iks.surveytool.services;

import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.SurveyResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SurveyResponseService {
    ResponseEntity<SurveyResponseDTO> processSurveyResponseDTOs(SurveyResponseDTO surveyResponseDTO);

    boolean validateUniqueParticipation(SurveyResponse surveyResponse);

    ResponseEntity<List<SurveyResponseDTO>> processSurveyResponseDTOs(String accessId, String authToken);
    ResponseEntity<?> deleteSurveyResponsesByUserId(long id);
    ResponseEntity<?> anonymizeSurveyResponsesByUserId(long id);
}
