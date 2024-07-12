package iks.surveytool.services;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface SurveyService {
    ResponseEntity<SurveyOverviewDTO> createSurvey(CompleteSurveyDTO surveyDTO, Jwt jwttoken) throws InvalidEntityException;
    ResponseEntity<SurveyOverviewDTO> editSurvey(String accessId, CompleteSurveyDTO completeSurveyDTO, Jwt jwttoken)
        throws InvalidEntityException;
    ResponseEntity<? extends SurveyOverviewDTO> getSurveyByAccessId(String accessId, Jwt jwttoken);
    ResponseEntity<? extends SurveyOverviewDTO> getSurveyByParticipationId(String participationId);
    ResponseEntity<List<SurveyOverviewDTO>> getCurrentOpenSurveys();
    ResponseEntity<List<SurveyOverviewDTO>> getUserSurveys(Jwt jwttoken);
    ResponseEntity<?> deleteSurveysByUserId(long id);
    ResponseEntity<?> anonymizeSurveysByUserId(long id);
}