package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/createSurvey")
public class CreateSurveyController {

    private final SurveyService surveyService;

    public CreateSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("")
    public String surveyForm(Model model) {
        model.addAttribute("newSurvey", new Survey());
        return "createSurvey";
    }

    @PostMapping("")
    public String postSurveyForm(@ModelAttribute("newSurvey") Survey survey,
                                 @RequestParam("start")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         LocalDateTime startDate,
                                 @RequestParam("end")
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                         LocalDateTime endDate) {
        surveyService.addSurvey(survey, startDate, endDate);
        return "redirect:/";
    }
}
