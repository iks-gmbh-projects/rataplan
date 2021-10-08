package iks.surveytool.controller;

import iks.surveytool.entities.CheckboxGroup;
import iks.surveytool.entities.Question;
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
        Survey survey = surveyService.findSurveyById(surveyID);
        model.addAttribute("survey", survey);
        model.addAttribute("newQuestionGroup", new QuestionGroup());
        model.addAttribute("newQuestion", new Question());
        model.addAttribute("newCheckboxGroup", new CheckboxGroup());
        return "addQuestions";
    }

    @PostMapping("/questions/addGroup/{surveyID}")
    public String addQuestionGroup(@ModelAttribute("newQuestionGroup") QuestionGroup questionGroup,
                                   @PathVariable("surveyID") Long surveyID,
                                   RedirectAttributes redirectAttributes) {
        surveyService.addQuestionGroupToSurvey(questionGroup, surveyID);

        redirectAttributes.addAttribute("surveyID", surveyID);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/questions/addQuestion/{surveyID}/{questionGroupID}")
    public String addQuestionToGroup(@ModelAttribute("newQuestion") Question question,
                                     @ModelAttribute("newCheckboxGroup") CheckboxGroup checkboxGroup,
                                     @PathVariable("surveyID") Long surveyID,
                                     @PathVariable("questionGroupID") Long questionGroupID,
                                     RedirectAttributes redirectAttributes) {
        surveyService.addQuestionToQuestionGroup(question, checkboxGroup, questionGroupID);

        redirectAttributes.addAttribute("surveyID", surveyID);

        return "redirect:/createSurvey/questions";
    }
}
