package iks.surveytool.services;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface SurveyService {
    ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) throws InvalidEntityException;
    ResponseEntity<SurveyOverviewDTO> processEditSurveyByAccessId(String accessId, CompleteSurveyDTO completeSurveyDTO, Jwt jwttoken)
        throws InvalidEntityException;
    ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId, Jwt jwttoken);
    ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId);
    ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys();
    ResponseEntity<List<SurveyOverviewDTO>> processMySurveys(Jwt jwttoken);
    ResponseEntity<?> deleteSurveysByUserId(long id);
    ResponseEntity<?> anonymizeSurveysByUserId(long id);
}