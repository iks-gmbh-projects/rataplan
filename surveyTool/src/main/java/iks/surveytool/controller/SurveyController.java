package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;

@Controller
@CrossOrigin(origins = "*")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/createSurvey/save")
    public ResponseEntity<Survey> saveSurvey(@RequestBody Survey newSurvey) throws MalformedURLException {
        Long id = surveyService.addSurvey(newSurvey);
        Optional<Survey> savedSurvey = surveyService.findSurveyById(id);
        if (savedSurvey.isPresent()) {
            return ResponseEntity.ok(savedSurvey.get());
        }
        return null;
    }

    @GetMapping("/createSurvey/{surveyID}/final")
    public ResponseEntity<Survey> finalizeCreation(@PathVariable("surveyID") Long surveyID) {
        Optional<Survey> surveyOptional = surveyService.findSurveyById(surveyID);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return ResponseEntity.ok(survey);
        }
        return null;
    }

    @GetMapping("/answers")
    public ResponseEntity<Survey> getSurveyForAnswering(@RequestParam UUID uuid) {
        Optional<Survey> surveyOptional = surveyService.findSurveyByUUID(uuid);
        if (surveyOptional.isPresent()) {
            Survey survey = surveyOptional.get();
            return ResponseEntity.ok(survey);
        }
        return null;
    }
}
