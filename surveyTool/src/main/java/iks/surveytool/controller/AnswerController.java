package iks.surveytool.controller;

import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = "*")
public class AnswerController {

    SurveyService surveyService;

    public AnswerController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }
}

