package iks.surveytool.services;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.InvalidSurveyException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SurveyService {
    ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO) throws InvalidSurveyException;
    ResponseEntity<SurveyOverviewDTO> processEditSurveyByAccessId(String accessId, CompleteSurveyDTO completeSurveyDTO, String jwttoken)
        throws InvalidSurveyException;
    ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId, String jwttoken);
    ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId);
    ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys();
    ResponseEntity<List<SurveyOverviewDTO>> processMySurveys(String jwttoken);
    ResponseEntity<?> deleteSurveysByUserId(long id);
    ResponseEntity<?> anonymizeSurveysByUserId(long id);
}
