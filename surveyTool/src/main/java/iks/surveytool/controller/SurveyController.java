package iks.surveytool.controller;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/surveys")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<SurveyOverviewDTO> createSurvey(@RequestBody CompleteSurveyDTO surveyDTO) {
        Survey newSurvey = surveyService.createSurveyFromDTO(surveyDTO);
        if (surveyService.validate(newSurvey)) {
            surveyService.generateIds(newSurvey);
            Survey savedSurvey = surveyService.saveSurvey(newSurvey);
            SurveyOverviewDTO completeSurveyDTO = surveyService.createSurveyDTOFromSurvey(savedSurvey);
            return ResponseEntity.ok(completeSurveyDTO);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    @GetMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByAccessId(@RequestParam String accessId) {
        SurveyOverviewDTO surveyOverviewDTO = surveyService.createSurveyDTOByAccessId(accessId, true);
        if (surveyOverviewDTO != null) {
            return ResponseEntity.ok(surveyOverviewDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(params = {"participationId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByParticipationId(@RequestParam String participationId) {
        SurveyOverviewDTO surveyDTO = surveyService.createSurveyDTOByParticipationId(participationId);
        if (surveyDTO != null) {
            if (surveyDTO instanceof CompleteSurveyDTO) {
                return ResponseEntity.ok(surveyDTO);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(surveyDTO);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SurveyOverviewDTO>> findOpenAccessSurveys() {
        List<SurveyOverviewDTO> openAccessSurveys = surveyService.createSurveyDTOsByOpenIsTrue();
        return ResponseEntity.ok(openAccessSurveys);
    }
}
