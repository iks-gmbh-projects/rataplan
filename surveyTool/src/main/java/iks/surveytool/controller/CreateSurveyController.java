package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/createSurvey")
public class CreateSurveyController {

    SurveyService surveyService = new SurveyService();

    @GetMapping("")
    public String surveyForm(Model model) {
        model.addAttribute("newSurvey", new Survey());
        return "createSurvey";
    }
}
