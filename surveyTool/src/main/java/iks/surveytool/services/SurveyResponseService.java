package iks.surveytool.services;

import iks.surveytool.dtos.SurveyResponseDTO;
import iks.surveytool.entities.InvalidEntityException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface SurveyResponseService {
    ResponseEntity<SurveyResponseDTO> acceptSurveyResponse(SurveyResponseDTO surveyResponseDTO) throws
        InvalidEntityException;

    ResponseEntity<List<SurveyResponseDTO>> getSurveyResponses(String accessId, Jwt authToken);
    ResponseEntity<?> deleteSurveyResponsesByUserId(long id);
    ResponseEntity<?> anonymizeSurveyResponsesByUserId(long id);
}