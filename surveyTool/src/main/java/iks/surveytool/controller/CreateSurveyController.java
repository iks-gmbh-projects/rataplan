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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequestMapping("/createSurvey")
@SessionAttributes("survey")
public class CreateSurveyController {

    private final SurveyService surveyService;

    public CreateSurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @ModelAttribute("survey")
    public Survey survey() {
        return new Survey();
    }

    @GetMapping("")
    public String surveyForm(@ModelAttribute("survey") Survey survey,
                             Model model, HttpServletRequest request,
                             HttpSession session) {
        model.addAttribute("survey", survey);
        return "createSurvey";
    }

    @PostMapping("")
    public String postSurveyForm(@Valid @ModelAttribute("survey") Survey newSurvey,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        // TODO: Validate that endDate is after startDate
        if (!bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("survey", newSurvey);
        } else {
            return "createSurvey";
        }

        return "redirect:createSurvey/questions";
    }

    @GetMapping("/questions")
    public String addQuestions(@ModelAttribute("survey") Survey survey, Model model) {
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
