package iks.surveytool.controller;

import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/createSurvey")
public class CreateSurveyController {

    SurveyService surveyService = new SurveyService();

    @GetMapping("")
    public String start() {

        return "createSurvey";
    }
}
