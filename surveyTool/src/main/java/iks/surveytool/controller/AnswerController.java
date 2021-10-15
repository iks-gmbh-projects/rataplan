package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/answer")
public class AnswerController {

    SurveyService surveyService;

    public AnswerController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("")
    public String getSurvey(@RequestParam("surveyUUID") UUID uuid,
                            Model model) {
        Optional<Survey> surveyOptional = surveyService.findSurveyByUUID(uuid);

        if (surveyOptional.isPresent()) {
            model.addAttribute("survey", surveyOptional.get());
        } else {
            model.addAttribute("notAvailable",
                    "Eine Umfrage mit der UUID '" + uuid + "' ist leider nicht vorhanden.");
        }

        return "answerSurvey";
    }
}
