package iks.surveytool.controller;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/surveys")
@CrossOrigin(origins = "*")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("")
    public ResponseEntity<SurveyOverviewDTO> postSurvey(@RequestBody CompleteSurveyDTO surveyDTO) {
        Survey newSurvey = surveyService.createSurveyFromDto(surveyDTO);
        Survey savedSurvey = surveyService.saveSurvey(newSurvey);
        SurveyOverviewDTO completeSurveyDTO = surveyService.createSurveyDtoFromSurvey(savedSurvey);
        return ResponseEntity.ok(completeSurveyDTO);
    }

    @GetMapping("{accessId}")
    public ResponseEntity<SurveyOverviewDTO> getSurveyByAccessID(@PathVariable String accessId) {
        SurveyOverviewDTO surveyOverviewDTO = surveyService.createSurveyDtoByAccessId(accessId, true);
        if (surveyOverviewDTO != null) {
            return ResponseEntity.ok(surveyOverviewDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("")
    public ResponseEntity<SurveyOverviewDTO> getSurveyForAnswering(@RequestParam UUID uuid) {
        SurveyOverviewDTO surveyDto = surveyService.createSurveyDtoByUUID(uuid);
        if (surveyDto != null) {
            if (surveyDto instanceof CompleteSurveyDTO) {
                return ResponseEntity.ok(surveyDto);
            } else {
                // If current time is not within start- and endDate: return survey without questions to fill information
                // in front-end
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(surveyDto);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/openAccess")
    public ResponseEntity<List<SurveyOverviewDTO>> getOpenAccessSurveys() {
        List<SurveyOverviewDTO> openAccessSurveys = surveyService.createSurveyDtosByOpenIsTrue();
        return ResponseEntity.ok(openAccessSurveys);
    }
}
