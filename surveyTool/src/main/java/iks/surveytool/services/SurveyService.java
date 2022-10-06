package iks.surveytool.services;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SurveyService {
    ResponseEntity<SurveyOverviewDTO> processSurveyDTO(CompleteSurveyDTO surveyDTO);
    ResponseEntity<SurveyOverviewDTO> processSurveyByAccessId(String accessId, String jwttoken);
    ResponseEntity<SurveyOverviewDTO> processSurveyByParticipationId(String participationId);
    ResponseEntity<List<SurveyOverviewDTO>> processOpenAccessSurveys();

}
