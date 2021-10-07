package iks.surveytool.controller;

import iks.surveytool.entities.QuestionGroup;
import iks.surveytool.entities.Survey;
import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
    public String postSurveyForm(@Valid @ModelAttribute("newSurvey") Survey newSurvey,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        // TODO: Validate that endDate is after startDate
        Long surveyID;
        if (!bindingResult.hasErrors()) {
            surveyID = surveyService.addSurvey(newSurvey);
            redirectAttributes.addAttribute("surveyID", surveyID);
        } else {
            return "createSurvey";
        }

        return "redirect:createSurvey/questions";
    }

    @GetMapping("/questions")
    public String addQuestions(@RequestParam Long surveyID, Model model) {
        Survey survey = surveyService.findById(surveyID);
        model.addAttribute("survey", survey);
        model.addAttribute("newQuestionGroup", new QuestionGroup());
        return "addQuestions";
    }

    @PostMapping("/questions/addGroup/{id}")
    public String addQuestionGroup(@ModelAttribute("newQuestionGroup") QuestionGroup questionGroup,
                                   @PathVariable("id") Long surveyID,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Survey survey = surveyService.findById(surveyID);
        surveyService.addQuestionGroupToSurvey(questionGroup, survey);

        model.addAttribute("survey", survey);
        model.addAttribute("newQuestionGroup", new QuestionGroup());
        redirectAttributes.addAttribute("surveyID", surveyID);

        return "redirect:/createSurvey/questions";
    }
}
