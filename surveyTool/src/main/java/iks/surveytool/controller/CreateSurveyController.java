package iks.surveytool.controller;

import iks.surveytool.entities.*;
import iks.surveytool.services.SurveyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public Survey getNewSurvey() {
        return new Survey();
    }

    @GetMapping("")
    public String surveyForm(@ModelAttribute("survey") Survey survey, Model model) {
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
        model.addAttribute("newCheckbox", new Checkbox());
        return "addQuestions";
    }

    @PostMapping("/questions/addGroup")
    public String addQuestionGroup(@ModelAttribute("survey") Survey survey,
                                   @ModelAttribute("newQuestionGroup") QuestionGroup questionGroup,
                                   RedirectAttributes redirectAttributes) {
        surveyService.addQuestionGroupToSurvey(survey, questionGroup);

        redirectAttributes.addFlashAttribute("survey", survey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/questions/addQuestion/{questionGroupIndex}")
    public String addQuestionToGroup(@ModelAttribute("survey") Survey survey,
                                     @ModelAttribute("newQuestion") Question question,
                                     @ModelAttribute("newCheckboxGroup") CheckboxGroup checkboxGroup,
                                     @PathVariable("questionGroupIndex") int questionGroupIndex,
                                     RedirectAttributes redirectAttributes) {
        surveyService.addQuestionToQuestionGroup(survey, questionGroupIndex, question, checkboxGroup);

        redirectAttributes.addFlashAttribute("survey", survey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/questions/addQuestion/{questionGroupIndex}/{questionIndex}")
    public String addCheckboxToQuestion(@ModelAttribute("survey") Survey survey,
                                        @ModelAttribute("newCheckbox") Checkbox checkbox,
                                        @PathVariable("questionGroupIndex") int questionGroupIndex,
                                        @PathVariable("questionIndex") int questionIndex,
                                        RedirectAttributes redirectAttributes) {
        surveyService.addCheckboxToQuestion(survey, questionGroupIndex, questionIndex, checkbox);

        redirectAttributes.addFlashAttribute("survey", survey);

        return "redirect:/createSurvey/questions";
    }

    @PostMapping("/save")
    public String saveSurvey(@ModelAttribute("survey") Survey survey, SessionStatus status) {
        surveyService.addSurvey(survey);

        // Remove survey as session attribute
        status.setComplete();

        return "redirect:/";
    }
}
