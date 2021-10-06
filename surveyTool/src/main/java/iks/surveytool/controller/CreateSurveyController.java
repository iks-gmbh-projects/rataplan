package iks.surveytool.controller;

import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/createSurvey")
public class CreateSurveyController {

    private final SurveyService surveyService;

    public CreateSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    // TODO: Fix formatting of start-/endDate
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping("")
    public String surveyForm(Model model) {
        model.addAttribute("newSurvey", new Survey());
        return "createSurvey";
    }

    @PostMapping("")
    public String postSurveyForm(@ModelAttribute("newSurvey") Survey survey) {
        surveyService.addSurvey(survey);
        return "redirect:/";
    }
}
