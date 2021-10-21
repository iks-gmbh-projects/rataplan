package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.util.Optional;

@Controller
@RequestMapping("/createSurvey")
@CrossOrigin(origins = "*")
public class SurveyController {

    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @PostMapping("/save")
    public ResponseEntity<Survey> saveSurvey(@RequestBody Survey newSurvey) throws MalformedURLException {
        Long id = surveyService.addSurvey(newSurvey);
        Optional<Survey> savedSurvey = surveyService.findSurveyById(id);
        if (savedSurvey.isPresent()) {
            return ResponseEntity.ok(savedSurvey.get());
        }
        return null;
    }

//    @GetMapping("/{surveyID}/final")
//    public String finalizeCreation(@Valid @PathVariable("surveyID") Long surveyID, Model model) throws MalformedURLException {
//        Optional<Survey> surveyOptional = surveyService.findSurveyById(surveyID);
//        if (surveyOptional.isPresent()) {
//            Survey survey = surveyOptional.get();
//            model.addAttribute("survey", survey);
//
//            URL url = new URL("http", "localhost", 8080, "/answer?surveyUUID=" + survey.getUuid());
//            model.addAttribute("url", url);
//        } else {
//            model.addAttribute("notAvailable",
//                    "Eine Umfrage mit der ID '" + surveyID + "' ist leider nicht vorhanden.");
//        }
//
//        return "finalizeSurvey";
//    }
}
